package fr.ffnet.downloader.fanfiction.reviews

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fr.ffnet.downloader.databinding.ItemReviewBinding
import fr.ffnet.downloader.fanfiction.reviews.ReviewsListAdapter.ReviewViewHolder
import fr.ffnet.downloader.models.ReviewUI

class ReviewsListAdapter : RecyclerView.Adapter<ReviewViewHolder>() {

    var reviewList: List<ReviewUI> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val binding = ItemReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(reviewList[position])
    }

    override fun getItemCount(): Int = reviewList.size

    inner class ReviewViewHolder(
        private val view: ItemReviewBinding
    ) : RecyclerView.ViewHolder(view.root) {
        fun bind(review: ReviewUI) {
            view.posterTextView.text = review.poster
            view.chapterTextView.text = review.chapter
            view.commentTextView.text = review.comment
        }
    }
}
