package fr.ffnet.downloader.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.observe
import com.google.android.material.snackbar.Snackbar
import fr.ffnet.downloader.R
import fr.ffnet.downloader.common.MainApplication
import fr.ffnet.downloader.main.injection.ViewPagerModule
import fr.ffnet.downloader.main.search.SearchFragment
import fr.ffnet.downloader.main.synced.SyncedFragment
import fr.ffnet.downloader.options.ParentListener
import fr.ffnet.downloader.settings.SettingsFragment
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : AppCompatActivity(), ParentListener {

    @Inject lateinit var viewModel: ViewPagerViewModel

    private var hasStories: Boolean = false

    companion object {
        private const val MENU_IMAGE_SPEED = 1.5f

        private const val FRAGMENT_ID_SETTINGS = 0
        private const val FRAGMENT_ID_SYNCED = 1
        private const val FRAGMENT_ID_SEARCH = 2
    }

    private var getBackFromSettings: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MainApplication
            .getComponent(this)
            .plus(ViewPagerModule(this))
            .inject(this)

        menuImageView.frame = 30
        menuImageView.setMinAndMaxFrame(30, 59)

        searchIcon.setOnClickListener {
            showSearch()
        }

        initViewPager()

        menuImageView.setOnClickListener {
            when (menuImageView.frame) {
                in 50..59 -> onBackPressed()
                in 25..35 -> {
                    getBackFromSettings = pageTypeViewPager.currentItem
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
    }

    override fun onResume() {
        figureOutMenuIcon()
        super.onResume()
    }

    override fun showErrorMessage(message: String) {
        Snackbar.make(mainContainer, message, Snackbar.LENGTH_LONG).show()
    }

    override fun onBackPressed() {
        val shouldShowWelcomeBlock = getSearchFragment()?.shouldShowWelcomeBlock() ?: false
        if ((pageTypeViewPager.currentItem == FRAGMENT_ID_SEARCH && hasStories) || shouldShowWelcomeBlock) {
            getSearchFragment()?.onBackPressed()
        } else {
            super.onBackPressed()
        }
    }

    private fun initViewPager() {
        val mainAdapter = MainAdapter(supportFragmentManager)
        pageTypeViewPager.adapter = mainAdapter
        viewModel.hasSyncedStories().observe(this) { newHasStories ->
            hasStories = newHasStories
            if (mainAdapter.fragmentList.isEmpty()) {
                pageTypeViewPager.adapter = mainAdapter.apply {
                    fragmentList = mutableListOf(
                        SettingsFragment(),
                        SyncedFragment(),
                        SearchFragment()
                    )
                }
            }
            if (hasStories) {
                showSynced()
            } else {
                showSearch()
            }
            figureOutMenuIcon()
        }
    }

    private fun showSettings() {
        pageTypeViewPager.currentItem = FRAGMENT_ID_SETTINGS
        figureOutMenuIcon()
    }

    fun showSynced() {
        if (hasStories.not()) {
            super.onBackPressed()
        } else {
            pageTypeViewPager.currentItem = FRAGMENT_ID_SYNCED
            figureOutMenuIcon()
        }
    }

    private fun showSearch() {
        pageTypeViewPager.currentItem = FRAGMENT_ID_SEARCH
        figureOutMenuIcon()
    }

    private fun setMenuClose() {
        if (menuImageView.frame in 25..35) {
            menuImageView.setMinAndMaxFrame(0, 30)
            menuImageView.speed = -MENU_IMAGE_SPEED
            menuImageView.playAnimation()
        } else if (menuImageView.frame in 50..59) {
            menuImageView.setMinAndMaxFrame(0, 59)
            menuImageView.speed = -MENU_IMAGE_SPEED
            menuImageView.playAnimation()
        }
    }

    private fun setMenuBack() {
        if (menuImageView.frame in 25..35) {
            menuImageView.setMinAndMaxFrame(30, 59)
            menuImageView.speed = MENU_IMAGE_SPEED
            menuImageView.playAnimation()
        } else if (menuImageView.frame in 0..10) {
            menuImageView.setMinAndMaxFrame(0, 59)
            menuImageView.speed = MENU_IMAGE_SPEED
            menuImageView.playAnimation()
        }
    }

    private fun setMenuOriginal() {
        if (menuImageView.frame in 0..10) {
            menuImageView.setMinAndMaxFrame(0, 30)
            menuImageView.speed = MENU_IMAGE_SPEED
            menuImageView.playAnimation()
        } else if (menuImageView.frame in 50..59) {
            menuImageView.setMinAndMaxFrame(30, 59)
            menuImageView.speed = -MENU_IMAGE_SPEED
            menuImageView.playAnimation()
        }
    }

    private fun figureOutMenuIcon() {
        searchIcon.isVisible = isOnSyncedFragment()
        when {
            isOnSyncedFragment() -> setMenuOriginal()
            hasStories && isOnSearchFragment() -> setMenuBack()
            isOnSearchFragment() -> setMenuOriginal()
            isOnSettingsFragment() -> setMenuClose()
        }
    }

    private fun getSearchFragment(): SearchFragment? {
        return supportFragmentManager
            .fragments
            .filterIsInstance(SearchFragment::class.java)
            .firstOrNull()
    }

    private fun isOnSyncedFragment(): Boolean {
        return pageTypeViewPager.currentItem == FRAGMENT_ID_SYNCED
    }

    private fun isOnSearchFragment(): Boolean {
        return pageTypeViewPager.currentItem == FRAGMENT_ID_SEARCH
    }

    private fun isOnSettingsFragment(): Boolean {
        return pageTypeViewPager.currentItem == FRAGMENT_ID_SETTINGS
    }
}
