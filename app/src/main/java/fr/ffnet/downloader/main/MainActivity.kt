package fr.ffnet.downloader.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.lifecycle.observe
import com.google.android.material.snackbar.Snackbar
import fr.ffnet.downloader.R
import fr.ffnet.downloader.common.MainApplication
import fr.ffnet.downloader.main.SideNavFragment.DrawerMenuItem
import fr.ffnet.downloader.main.injection.ViewPagerModule
import fr.ffnet.downloader.main.search.SearchFragment
import fr.ffnet.downloader.main.synced.SyncedFragment
import fr.ffnet.downloader.options.ParentListener
import fr.ffnet.downloader.settings.SettingsActivity
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : AppCompatActivity(), ParentListener {

    @Inject lateinit var viewModel: ViewPagerViewModel

    private var hasStories: Boolean = false

    companion object {
        private const val MENU_IMAGE_SPEED = 1.5f

        private const val FRAGMENT_ID_SYNCED = 0
        private const val FRAGMENT_ID_SEARCH = 1
    }

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
            if (menuImageView.frame > 50) {
                onBackPressed()
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }
    }

    override fun onResume() {
        if (isOnSyncedFragment()) {
            setMenuOriginal()
        } else if (hasStories) {
            setMenuBack()
        }
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

    fun showSynced() {
        if (hasStories.not()) {
            super.onBackPressed()
        } else {
            setMenuOriginal()
            searchIcon.isVisible = true
            pageTypeViewPager.currentItem = FRAGMENT_ID_SYNCED
        }
    }

    fun onMenuItemClicked(menuItem: DrawerMenuItem) {
        when (menuItem) {
            DrawerMenuItem.SETTINGS -> startActivity(SettingsActivity.newIntent(this))
        }
        drawerLayout.closeDrawer(GravityCompat.START)
    }

    private fun initViewPager() {
        val mainAdapter = MainAdapter(supportFragmentManager)
        pageTypeViewPager.adapter = mainAdapter
        viewModel.hasSyncedStories().observe(this) { newHasStories ->
            hasStories = newHasStories
            if (mainAdapter.fragmentList.isEmpty()) {
                pageTypeViewPager.adapter = mainAdapter.apply {
                    fragmentList = mutableListOf(
                        SyncedFragment.newInstance(),
                        SearchFragment.newInstance()
                    )
                }
            }
            if (hasStories.not()) {
                showSearch()
            }
            searchIcon.isVisible = isOnSyncedFragment()
        }
    }

    private fun showSearch() {
        if (hasStories) {
            setMenuBack()
        }
        searchIcon.isVisible = false
        pageTypeViewPager.currentItem = FRAGMENT_ID_SEARCH
    }

    private fun setMenuBack() {
        menuImageView.speed = MENU_IMAGE_SPEED
        menuImageView.playAnimation()
    }

    private fun setMenuOriginal() {
        menuImageView.speed = -MENU_IMAGE_SPEED
        menuImageView.playAnimation()
    }

    private fun getSearchFragment(): SearchFragment? {
        return supportFragmentManager
            .fragments
            .filterIsInstance(SearchFragment::class.java)
            .firstOrNull()
    }

    private fun getSyncedFragment(): SyncedFragment? {
        return supportFragmentManager
            .fragments
            .filterIsInstance(SyncedFragment::class.java)
            .firstOrNull()
    }

    private fun isOnSyncedFragment(): Boolean {
        return pageTypeViewPager.currentItem == FRAGMENT_ID_SYNCED && getSyncedFragment() != null
    }
}
