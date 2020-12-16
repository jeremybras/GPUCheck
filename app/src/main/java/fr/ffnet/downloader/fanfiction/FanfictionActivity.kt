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
import fr.ffnet.downloader.databinding.ActivityFanfictionBinding
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
import javax.inject.Inject

class FanfictionActivity : AppCompatActivity(), ParentListener, PopupMenu.OnMenuItemClickListener {

    @Inject lateinit var settingsViewModel: SettingsViewModel
    @Inject lateinit var viewModel: FanfictionViewModel
    @Inject lateinit var optionsController: OptionsController
    @Inject lateinit var picasso: Picasso

    private val fanfictionId by lazy { intent.getStringExtra(EXTRA_ID) ?: "" }
    private lateinit var binding: ActivityFanfictionBinding

    companion object {

        private const val EXTRA_ID = "EXTRA_ID"

        fun newIntent(context: Context, fanfictionId: String): Intent = Intent(
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

        binding = ActivityFanfictionBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
                optionsController.onUnsyncStory(fanfictionId)
                true
            }
            else -> false
        }
    }

    override fun showErrorMessage(message: String) {
        Snackbar.make(binding.containerView, message, Snackbar.LENGTH_LONG).show()
    }

    private fun setObservers() {
        viewModel.loadFanfictionInfo(fanfictionId).observe(this) { story ->

            picasso
                .load("https:${story.imageUrl}")
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(binding.storyImageView)

            binding.titleTextView.text = story.title
            binding.detailsTextView.text = story.details
            binding.publishedDateTextView.text = story.publishedDate
            binding.updatedDateTextView.text = story.updatedDate

            binding.chapterSyncImageView.isVisible = story.isDownloadComplete.not()
            binding.chapterSyncTextView.isVisible = story.isDownloadComplete.not()
            binding.chapterSyncTextView.text = story.chaptersMissingText
        }
        viewModel.globalSyncState.observe(this) { storyState ->
            binding.actionButtonViewFlipper.displayedChild = when (storyState) {
                StoryState.Default -> DISPLAY_ADD_LIBRARY
                StoryState.Syncing -> DISPLAY_SYNCING
                StoryState.Synced -> DISPLAY_SYNCED
            }
        }
        settingsViewModel.settingList.observe(this) { settingList ->
            binding.exportPDFButton.isVisible = settingList
                .firstOrNull { it.type == PDF_EXPORT }
                ?.isEnabled
                ?: true
            binding.exportEPUBButton.isVisible = settingList
                .firstOrNull { it.type == EPUB_EXPORT }
                ?.isEnabled
                ?: true
        }
    }

    private fun setListeners() {
        binding.menuImageView.frame = 59
        binding.menuImageView.setOnClickListener {
            finish()
        }
        binding.optionsImageView.setOnClickListener {
            val popup = PopupMenu(this, binding.optionsImageView)
            val inflater = popup.menuInflater
            inflater.inflate(R.menu.story_menu, popup.menu)
            popup.setOnMenuItemClickListener(this)
            popup.show()
        }

        binding.addToLibraryButton.setOnClickListener {
            viewModel.syncChapters(fanfictionId)
        }
        binding.syncStoryButton.setOnClickListener {
            viewModel.syncChapters(fanfictionId)
        }
        binding.exportEPUBButton.setOnClickListener {
            optionsController.onExportEpub(fanfictionId)
        }
        binding.exportPDFButton.setOnClickListener {
            optionsController.onExportPdf(fanfictionId)
        }
    }

    private fun initializeTabLayout() {
        val betweenSpace = 30
        val slidingTabStrip = binding.detailsTabLayout.getChildAt(0) as ViewGroup
        for (i in 0 until slidingTabStrip.childCount - 1) {
            (slidingTabStrip.getChildAt(i).layoutParams as ViewGroup.MarginLayoutParams).rightMargin = betweenSpace
        }

        binding.viewPager.adapter = DetailsTabAdapter(
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
        binding.detailsTabLayout.setupWithViewPager(binding.viewPager)
    }
}
