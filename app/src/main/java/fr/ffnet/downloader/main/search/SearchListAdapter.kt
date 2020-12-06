package fr.ffnet.downloader.main.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import fr.ffnet.downloader.R
import fr.ffnet.downloader.models.SearchUIItem
import fr.ffnet.downloader.models.SearchUIItem.SearchAuthorUI
import fr.ffnet.downloader.models.SearchUIItem.SearchStoryUI
import fr.ffnet.downloader.models.SearchUIItem.SearchUITitle
import fr.ffnet.downloader.options.OnFanfictionActionsListener
import kotlinx.android.synthetic.main.item_search_result_story.view.*
import kotlinx.android.synthetic.main.item_search_result_title.view.*

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
            TYPE_HEADER -> FanfictionUITitleViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_search_result_title, parent, false
                )
            )
            TYPE_AUTHOR -> SearchAuthorUIViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_search_result_author, parent, false
                )
            )
            else -> SearchStoryUIViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_search_result_story, parent, false
                )
            )
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

    inner class FanfictionUITitleViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: SearchUITitle) {
            view.searchResultTitleTextView.text = item.title
        }
    }

    inner class SearchAuthorUIViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(author: SearchAuthorUI) {

        }
    }

    inner class SearchStoryUIViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(story: SearchStoryUI) {

            picasso
                .load(story.imageUrl)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(view.storyImageView)

            view.storyTitleTextView.text = story.title
            view.detailsTextView.text = story.details
            view.updatedDateTextView.text = story.updatedDate

            if (story.language.isEmpty()) {
                view.languageButton.isVisible = false
            }
            if (story.genre.isEmpty()) {
                view.genreButton.isVisible = false
            }
            view.languageButton.text = story.language
            view.genreButton.text = story.genre
            view.chaptersButton.text = story.chaptersNb.toString()
            view.favoritesButton.text = story.favoritesNb
            view.reviewsButton.text = story.reviewsNb

            view.addButton.setOnClickListener {
                listener.onFetchInformation(story.id)
            }
        }
    }
}
