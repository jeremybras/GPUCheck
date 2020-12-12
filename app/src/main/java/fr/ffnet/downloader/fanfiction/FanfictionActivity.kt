package fr.ffnet.downloader.fanfiction

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.observe
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import fr.ffnet.downloader.BuildConfig
import fr.ffnet.downloader.R
import fr.ffnet.downloader.common.MainApplication
import fr.ffnet.downloader.fanfiction.FanfictionViewModel.StoryState
import fr.ffnet.downloader.fanfiction.chapters.FanfictionDetailsChaptersFragment
import fr.ffnet.downloader.fanfiction.injection.FanfictionModule
import fr.ffnet.downloader.fanfiction.reviews.FanfictionDetailsReviewsFragment
import fr.ffnet.downloader.fanfiction.summary.FanfictionDetailsSummaryFragment
import fr.ffnet.downloader.models.SettingType.EPUB_EXPORT
import fr.ffnet.downloader.models.SettingType.PDF_EXPORT
import fr.ffnet.downloader.options.OptionsController
import fr.ffnet.downloader.options.ParentListener
import fr.ffnet.downloader.settings.SettingsViewModel
import kotlinx.android.synthetic.main.activity_fanfiction.*
import javax.inject.Inject

class FanfictionActivity : AppCompatActivity(), ParentListener, PopupMenu.OnMenuItemClickListener {

    @Inject lateinit var settingsViewModel: SettingsViewModel
    @Inject lateinit var viewModel: FanfictionViewModel
    @Inject lateinit var optionsController: OptionsController
    @Inject lateinit var picasso: Picasso

    private val fanfictionId by lazy { intent.getStringExtra(EXTRA_ID) ?: "" }

    companion object {

        private const val EXTRA_ID = "EXTRA_ID"

        fun intent(context: Context, fanfictionId: String): Intent = Intent(
            context, FanfictionActivity::class.java
        ).apply {
            putExtra(EXTRA_ID, fanfictionId)
        }

        private const val DISPLAY_ADD_LIBRARY = 0
        private const val DISPLAY_SYNCING = 2
        private const val DISPLAY_SYNCED = 3
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MainApplication.getComponent(this)
            .plus(FanfictionModule(this))
            .inject(this)

        setContentView(R.layout.activity_fanfiction)

        initializeTabLayout()
        setListeners()
        setObservers()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.story_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.viewOnWebOption -> {
                true
            }
            R.id.deleteOption -> {
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.viewOnWebOption -> {
                val browserIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("${BuildConfig.API_MOBILE_BASE_URL}s/$fanfictionId")
                )
                startActivity(browserIntent)
                true
            }
            R.id.deleteOption -> {
                optionsController.onUnsync(fanfictionId)
                true
            }
            else -> false
        }
    }

    override fun showErrorMessage(message: String) {
        Snackbar.make(containerView, message, Snackbar.LENGTH_LONG).show()
    }

    private fun setObservers() {
        viewModel.loadFanfictionInfo(fanfictionId).observe(this) { story ->

            picasso
                .load("https:${story.imageUrl}")
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(storyImageView)

            titleTextView.text = story.title
            detailsTextView.text = story.details
            publishedDateTextView.text = story.publishedDate
            updatedDateTextView.text = story.updatedDate

            chapterSyncImageView.isVisible = story.isDownloadComplete.not()
            chapterSyncTextView.isVisible = story.isDownloadComplete.not()
            chapterSyncTextView.text = story.chaptersMissingText
        }
        viewModel.globalSyncState.observe(this) { storyState ->
            actionButtonViewFlipper.displayedChild = when (storyState) {
                StoryState.Default -> DISPLAY_ADD_LIBRARY
                StoryState.Syncing -> DISPLAY_SYNCING
                StoryState.Synced -> DISPLAY_SYNCED
            }
        }
        settingsViewModel.settingList.observe(this) { settingList ->
            exportPDFButton.isVisible = settingList
                .firstOrNull { it.type == PDF_EXPORT }
                ?.isEnabled
                ?: true
            exportEPUBButton.isVisible = settingList
                .firstOrNull { it.type == EPUB_EXPORT }
                ?.isEnabled
                ?: true
        }
    }

    private fun setListeners() {
        menuImageView.frame = 59
        menuImageView.setOnClickListener {
            finish()
        }
        optionsImageView.setOnClickListener {
            val popup = PopupMenu(this, optionsImageView)
            val inflater = popup.menuInflater
            inflater.inflate(R.menu.story_menu, popup.menu)
            popup.setOnMenuItemClickListener(this)
            popup.show()
        }

        addToLibraryButton.setOnClickListener {
            viewModel.syncChapters(fanfictionId)
        }
        syncStoryButton.setOnClickListener {
            viewModel.syncChapters(fanfictionId)
        }
        exportEPUBButton.setOnClickListener {
            optionsController.onExportEpub(fanfictionId)
        }
        exportPDFButton.setOnClickListener {
            optionsController.onExportPdf(fanfictionId)
        }
    }

    private fun initializeTabLayout() {
        val betweenSpace = 30
        val slidingTabStrip = detailsTabLayout.getChildAt(0) as ViewGroup
        for (i in 0 until slidingTabStrip.childCount - 1) {
            (slidingTabStrip.getChildAt(i).layoutParams as ViewGroup.MarginLayoutParams).rightMargin = betweenSpace
        }

        viewPager.adapter = DetailsTabAdapter(
            supportFragmentManager,
            mapOf(
                resources.getString(R.string.story_tab_title_summary) to FanfictionDetailsSummaryFragment.newInstance(
                    fanfictionId
                ),
                resources.getString(R.string.story_tab_title_chapters) to FanfictionDetailsChaptersFragment.newInstance(
                    fanfictionId
                ),
                resources.getString(R.string.story_tab_title_reviews) to FanfictionDetailsReviewsFragment.newInstance(
                    fanfictionId
                )
            )
        )
        detailsTabLayout.setupWithViewPager(viewPager)
    }
}
