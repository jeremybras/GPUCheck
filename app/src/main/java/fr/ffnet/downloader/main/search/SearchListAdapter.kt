package fr.ffnet.downloader.main.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import fr.ffnet.downloader.R
import fr.ffnet.downloader.databinding.ItemSearchResultAuthorBinding
import fr.ffnet.downloader.databinding.ItemSearchResultStoryBinding
import fr.ffnet.downloader.databinding.ItemSearchResultTitleBinding
import fr.ffnet.downloader.models.SearchUIItem
import fr.ffnet.downloader.models.SearchUIItem.SearchAuthorUI
import fr.ffnet.downloader.models.SearchUIItem.SearchStoryUI
import fr.ffnet.downloader.models.SearchUIItem.SearchUITitle
import fr.ffnet.downloader.options.OnFanfictionActionsListener

class SearchListAdapter(
    private val picasso: Picasso,
    private val listener: OnFanfictionActionsListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_AUTHOR = 1
        private const val TYPE_STORY = 2
    }

    var searchResultList: List<SearchUIItem> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_HEADER -> {
                FanfictionUITitleViewHolder(
                    ItemSearchResultTitleBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            TYPE_AUTHOR -> {
                SearchAuthorUIViewHolder(
                    ItemSearchResultAuthorBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            else -> {
                SearchStoryUIViewHolder(
                    ItemSearchResultStoryBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }

    override fun getItemViewType(position: Int): Int =
        when {
            searchResultList[position] is SearchUITitle -> TYPE_HEADER
            searchResultList[position] is SearchAuthorUI -> TYPE_AUTHOR
            else -> TYPE_STORY
        }

    override fun getItemCount(): Int = searchResultList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = searchResultList[position]) {
            is SearchUITitle -> (holder as FanfictionUITitleViewHolder).bind(item)
            is SearchAuthorUI -> (holder as SearchAuthorUIViewHolder).bind(item)
            is SearchStoryUI -> (holder as SearchStoryUIViewHolder).bind(item)
        }
    }

    inner class FanfictionUITitleViewHolder(val binding: ItemSearchResultTitleBinding) : RecyclerView.ViewHolder(
        binding.root
    ) {
        fun bind(item: SearchUITitle) {
            binding.searchResultTitleTextView.text = item.title
        }
    }

    inner class SearchAuthorUIViewHolder(
        private val binding: ItemSearchResultAuthorBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(author: SearchAuthorUI) {

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

    inner class SearchStoryUIViewHolder(
        private val binding: ItemSearchResultStoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(story: SearchStoryUI) {

            picasso
                .load(story.imageUrl)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(binding.storyImageView)

            binding.storyTitleTextView.text = story.title
            binding.detailsTextView.text = story.details
            binding.updatedDateTextView.text = story.updatedDate

            if (story.language.isEmpty()) {
                binding.languageButton.isVisible = false
            }
            if (story.genre.isEmpty()) {
                binding.genreButton.isVisible = false
            }
            binding.languageButton.text = story.language
            binding.genreButton.text = story.genre
            binding.chaptersButton.text = story.chaptersNb.toString()
            binding.favoritesButton.text = story.favoritesNb
            binding.reviewsButton.text = story.reviewsNb

            binding.addButton.setOnClickListener {
                listener.onFetchInformation(story.id)
            }
        }
    }
}
