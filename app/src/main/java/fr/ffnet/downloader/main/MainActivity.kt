package fr.ffnet.downloader.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.observe
import com.google.android.material.snackbar.Snackbar
import fr.ffnet.downloader.common.MainApplication
import fr.ffnet.downloader.databinding.ActivityMainBinding
import fr.ffnet.downloader.main.injection.ViewPagerModule
import fr.ffnet.downloader.main.search.SearchFragment
import fr.ffnet.downloader.main.synced.SyncedFragment
import fr.ffnet.downloader.options.ParentListener
import fr.ffnet.downloader.settings.SettingsFragment
import javax.inject.Inject

class MainActivity : AppCompatActivity(), ParentListener {

    @Inject lateinit var viewModel: ViewPagerViewModel

    private var hasSyncedItems: Boolean = false
    private lateinit var binding: ActivityMainBinding

    companion object {
        private const val MENU_IMAGE_SPEED = 1.5f

        private const val FRAGMENT_ID_SETTINGS = 0
        private const val FRAGMENT_ID_SYNCED = 1
        private const val FRAGMENT_ID_SEARCH = 2
    }

    private var getBackFromSettings: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MainApplication
            .getComponent(this)
            .plus(ViewPagerModule(this))
            .inject(this)

        binding.menuImageView.frame = 30
        binding.menuImageView.setMinAndMaxFrame(30, 59)

        binding.searchIcon.setOnClickListener {
            showSearch()
        }

        initViewPager()

        binding.menuImageView.setOnClickListener {
            onMenuImageView()
        }
    }

    override fun onResume() {
        figureOutMenuIcon()
        super.onResume()
    }

    override fun showErrorMessage(message: String) {
        Snackbar.make(binding.mainContainer, message, Snackbar.LENGTH_LONG).show()
    }

    override fun onBackPressed() {
        val shouldShowWelcomeBlock = getSearchFragment()?.shouldShowWelcomeBlock() ?: false
        if ((isOnSearchFragment() && hasSyncedItems) || shouldShowWelcomeBlock) {
            getSearchFragment()?.onBackPressed()
        } else if (isOnSettingsFragment()) {
            onMenuImageView()
        } else {
            super.onBackPressed()
        }
    }

    private fun initViewPager() {
        val mainAdapter = MainAdapter(supportFragmentManager)
        binding.pageTypeViewPager.adapter = mainAdapter
        viewModel.hasSyncedItems().observe(this) { newHasSyncedItems ->
            hasSyncedItems = newHasSyncedItems
            if (mainAdapter.fragmentList.isEmpty()) {
                binding.pageTypeViewPager.adapter = mainAdapter.apply {
                    fragmentList = mutableListOf(
                        SettingsFragment(),
                        SyncedFragment(),
                        SearchFragment()
                    )
                }
            }
            if (hasSyncedItems) {
                showSynced()
            } else {
                showSearch()
            }
            figureOutMenuIcon()
        }
    }

    private fun showSettings() {
        binding.pageTypeViewPager.currentItem = FRAGMENT_ID_SETTINGS
        figureOutMenuIcon()
    }

    fun showSynced() {
        if (hasSyncedItems.not()) {
            super.onBackPressed()
        } else {
            binding.pageTypeViewPager.currentItem = FRAGMENT_ID_SYNCED
            figureOutMenuIcon()
        }
    }

    private fun showSearch() {
        binding.pageTypeViewPager.currentItem = FRAGMENT_ID_SEARCH
        figureOutMenuIcon()
    }

    private fun setMenuClose() {
        if (binding.menuImageView.frame in 25..35) {
            binding.menuImageView.setMinAndMaxFrame(0, 30)
            binding.menuImageView.speed = -MENU_IMAGE_SPEED
            binding.menuImageView.playAnimation()
        } else if (binding.menuImageView.frame in 50..59) {
            binding.menuImageView.setMinAndMaxFrame(0, 59)
            binding.menuImageView.speed = -MENU_IMAGE_SPEED
            binding.menuImageView.playAnimation()
        }
    }

    private fun setMenuBack() {
        if (binding.menuImageView.frame in 25..35) {
            binding.menuImageView.setMinAndMaxFrame(30, 59)
            binding.menuImageView.speed = MENU_IMAGE_SPEED
            binding.menuImageView.playAnimation()
        } else if (binding.menuImageView.frame in 0..10) {
            binding.menuImageView.setMinAndMaxFrame(0, 59)
            binding.menuImageView.speed = MENU_IMAGE_SPEED
            binding.menuImageView.playAnimation()
        }
    }

    private fun setMenuOriginal() {
        if (binding.menuImageView.frame in 0..10) {
            binding.menuImageView.setMinAndMaxFrame(0, 30)
            binding.menuImageView.speed = MENU_IMAGE_SPEED
            binding.menuImageView.playAnimation()
        } else if (binding.menuImageView.frame in 50..59) {
            binding.menuImageView.setMinAndMaxFrame(30, 59)
            binding.menuImageView.speed = -MENU_IMAGE_SPEED
            binding.menuImageView.playAnimation()
        }
    }

    private fun figureOutMenuIcon() {
        binding.searchIcon.isVisible = isOnSyncedFragment()
        when {
            isOnSyncedFragment() -> setMenuOriginal()
            hasSyncedItems && isOnSearchFragment() -> setMenuBack()
            isOnSearchFragment() -> setMenuOriginal()
            isOnSettingsFragment() -> setMenuClose()
        }
    }

    private fun onMenuImageView() {
        when (binding.menuImageView.frame) {
            in 50..59 -> onBackPressed()
            in 25..35 -> {
                getBackFromSettings = binding.pageTypeViewPager.currentItem
                showSettings()
            }
            in 0..10 -> {
                if (getBackFromSettings == FRAGMENT_ID_SYNCED) {
                    showSynced()
                } else if (getBackFromSettings == FRAGMENT_ID_SEARCH) {
                    showSearch()
                }
                getBackFromSettings = null
            }
        }
    }

    private fun getSearchFragment(): SearchFragment? {
        return supportFragmentManager
            .fragments
            .filterIsInstance(SearchFragment::class.java)
            .firstOrNull()
    }

    private fun isOnSyncedFragment(): Boolean {
        return binding.pageTypeViewPager.currentItem == FRAGMENT_ID_SYNCED
    }

    private fun isOnSearchFragment(): Boolean {
        return binding.pageTypeViewPager.currentItem == FRAGMENT_ID_SEARCH
    }

    private fun isOnSettingsFragment(): Boolean {
        return binding.pageTypeViewPager.currentItem == FRAGMENT_ID_SETTINGS
    }
}
