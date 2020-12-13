package fr.ffnet.downloader.main.synced

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import fr.ffnet.downloader.R
import fr.ffnet.downloader.fanfiction.FanfictionViewModel.StoryState
import fr.ffnet.downloader.models.SyncedUIItem
import fr.ffnet.downloader.models.SyncedUIItem.SyncedAuthorUI
import fr.ffnet.downloader.models.SyncedUIItem.SyncedStorySpotlightUI
import fr.ffnet.downloader.models.SyncedUIItem.SyncedStoryUI
import fr.ffnet.downloader.options.OnFanfictionActionsListener
import kotlinx.android.synthetic.main.item_synced_result_author.view.*
import kotlinx.android.synthetic.main.item_synced_result_story_spotlight.view.*

class SyncedListAdapter(
    private val picasso: Picasso,
    private val listener: OnFanfictionActionsListener,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_AUTHOR = 0
        private const val TYPE_SPOTLIGHT_STORY = 1
        private const val TYPE_STORY = 2

        private const val DISPLAY_SYNC = 0
        private const val DISPLAY_SYNCING = 1
        private const val DISPLAY_SYNCED = 2
    }

    var syncedResultList: List<SyncedUIItem> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_AUTHOR -> SyncedAuthorUIViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_synced_result_author, parent, false
                )
            )
            TYPE_STORY -> SyncedStoryUIViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_synced_result_story, parent, false
                )
            )
            else -> SyncedSpotlighStorytUIViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_synced_result_story_spotlight, parent, false
                )
            )
        }
    }

    override fun getItemViewType(position: Int): Int = when {
        syncedResultList[position] is SyncedAuthorUI -> TYPE_AUTHOR
        syncedResultList[position] is SyncedStoryUI -> TYPE_STORY
        else -> TYPE_SPOTLIGHT_STORY
    }

    override fun getItemCount(): Int = syncedResultList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = syncedResultList[position]) {
            is SyncedAuthorUI -> (holder as SyncedAuthorUIViewHolder).bind(item)
            is SyncedStoryUI -> (holder as SyncedStoryUIViewHolder).bind(item)
            is SyncedStorySpotlightUI -> (holder as SyncedSpotlighStorytUIViewHolder).bind(item)
        }
    }

    fun unsync(position: Int) {
        val item = syncedResultList[position]
        if (item is SyncedStorySpotlightUI) {
            listener.onUnsync(item.id)
        }
        if (item is SyncedStoryUI) {
            listener.onUnsync(item.id)
        }
    }

    inner class SyncedAuthorUIViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(author: SyncedAuthorUI) {
            view.authorNameTextView.text = author.title
            view.nbStoriesTextView.text = author.nbStories
        }
    }

    inner class SyncedStoryUIViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(story: SyncedStoryUI) {

            view.actionButtonViewFlipper.displayedChild = when (story.storyState) {
                StoryState.Default -> DISPLAY_SYNC
                StoryState.Syncing -> DISPLAY_SYNCING
                is StoryState.Synced -> DISPLAY_SYNCED
            }

            view.exportPDFButton.isVisible = story.shouldShowExportPdf
            view.exportEPUBButton.isVisible = story.shouldShowExportEpub

            view.titleTextView.text = story.title
            view.detailsTextView.text = story.details

            view.exportPDFButton.setOnClickListener {
                listener.onExportPdf(story.id)
            }
            view.exportEPUBButton.setOnClickListener {
                listener.onExportEpub(story.id)
            }
            view.syncStoryButton.setOnClickListener {
                listener.onSync(story.id)
            }
            view.setOnClickListener {
                listener.onFetchInformation(story.id)
            }
        }
    }

    inner class SyncedSpotlighStorytUIViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(story: SyncedStorySpotlightUI) {

            picasso
                .load(story.imageUrl)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(view.storyImageView)

            view.actionButtonViewFlipper.displayedChild = when (story.storyState) {
                StoryState.Default -> DISPLAY_SYNC
                StoryState.Syncing -> DISPLAY_SYNCING
                StoryState.Synced -> DISPLAY_SYNCED
            }

            view.exportPDFButton.isVisible = story.shouldShowExportPdf
            view.exportEPUBButton.isVisible = story.shouldShowExportEpub

            view.titleTextView.text = story.title
            view.detailsTextView.text = story.details

            view.setOnClickListener {
                listener.onFetchInformation(story.id)
            }
            view.exportPDFButton.setOnClickListener {
                listener.onExportPdf(story.id)
            }
            view.exportEPUBButton.setOnClickListener {
                listener.onExportEpub(story.id)
            }
            view.syncStoryButton.setOnClickListener {
                listener.onSync(story.id)
            }
        }
    }
}
