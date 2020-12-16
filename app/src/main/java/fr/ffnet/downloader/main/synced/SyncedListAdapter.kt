package fr.ffnet.downloader.main.synced

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import fr.ffnet.downloader.R
import fr.ffnet.downloader.databinding.ItemSyncedResultAuthorBinding
import fr.ffnet.downloader.databinding.ItemSyncedResultStoryBinding
import fr.ffnet.downloader.databinding.ItemSyncedResultStorySpotlightBinding
import fr.ffnet.downloader.fanfiction.FanfictionViewModel.StoryState
import fr.ffnet.downloader.models.SyncedUIItem
import fr.ffnet.downloader.models.SyncedUIItem.SyncedAuthorUI
import fr.ffnet.downloader.models.SyncedUIItem.SyncedStorySpotlightUI
import fr.ffnet.downloader.models.SyncedUIItem.SyncedStoryUI
import fr.ffnet.downloader.models.SyncedUIItem.SyncedUITitle
import fr.ffnet.downloader.options.OnFanfictionActionsListener

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
                ItemSyncedResultAuthorBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            TYPE_STORY -> SyncedStoryUIViewHolder(
                ItemSyncedResultStoryBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            else -> SyncedSpotlighStorytUIViewHolder(
                ItemSyncedResultStorySpotlightBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
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
        when (val item = syncedResultList[position]) {
            is SyncedStorySpotlightUI -> listener.onUnsyncStory(item.id)
            is SyncedStoryUI -> listener.onUnsyncStory(item.id)
            is SyncedAuthorUI -> listener.onUnsyncAuthor(item.id)
            is SyncedUITitle -> {
                // Not supposed to happen
            }
        }
    }

    inner class SyncedAuthorUIViewHolder(
        private val binding: ItemSyncedResultAuthorBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(author: SyncedAuthorUI) {

            picasso
                .load(author.imageUrl)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(binding.authorImageView)

            binding.authorNameTextView.text = author.name
            binding.nbStoriesTextView.text = author.nbStories
            binding.root.setOnClickListener {
                listener.onFetchAuthorInformation(author.id)
            }
        }
    }

    inner class SyncedStoryUIViewHolder(
        private val binding: ItemSyncedResultStoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(story: SyncedStoryUI) {

            binding.actionButtonViewFlipper.displayedChild = when (story.storyState) {
                StoryState.Default -> DISPLAY_SYNC
                StoryState.Syncing -> DISPLAY_SYNCING
                is StoryState.Synced -> DISPLAY_SYNCED
            }

            binding.exportPDFButton.isVisible = story.shouldShowExportPdf
            binding.exportEPUBButton.isVisible = story.shouldShowExportEpub

            binding.titleTextView.text = story.title
            binding.detailsTextView.text = story.details

            binding.exportPDFButton.setOnClickListener {
                listener.onExportPdf(story.id)
            }
            binding.exportEPUBButton.setOnClickListener {
                listener.onExportEpub(story.id)
            }
            binding.syncStoryButton.setOnClickListener {
                listener.onSync(story.id)
            }
            binding.root.setOnClickListener {
                listener.onFetchInformation(story.id)
            }
        }
    }

    inner class SyncedSpotlighStorytUIViewHolder(
        private val binding: ItemSyncedResultStorySpotlightBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(story: SyncedStorySpotlightUI) {

            picasso
                .load(story.imageUrl)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(binding.storyImageView)

            binding.actionButtonViewFlipper.displayedChild = when (story.storyState) {
                StoryState.Default -> DISPLAY_SYNC
                StoryState.Syncing -> DISPLAY_SYNCING
                StoryState.Synced -> DISPLAY_SYNCED
            }

            binding.exportPDFButton.isVisible = story.shouldShowExportPdf
            binding.exportEPUBButton.isVisible = story.shouldShowExportEpub

            binding.titleTextView.text = story.title
            binding.detailsTextView.text = story.details

            binding.root.setOnClickListener {
                listener.onFetchInformation(story.id)
            }
            binding.exportPDFButton.setOnClickListener {
                listener.onExportPdf(story.id)
            }
            binding.exportEPUBButton.setOnClickListener {
                listener.onExportEpub(story.id)
            }
            binding.syncStoryButton.setOnClickListener {
                listener.onSync(story.id)
            }
        }
    }
}
