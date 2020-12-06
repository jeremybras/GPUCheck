package fr.ffnet.downloader.fanfiction.reviews

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fr.ffnet.downloader.R
import fr.ffnet.downloader.fanfiction.reviews.ReviewsListAdapter.ReviewViewHolder
import fr.ffnet.downloader.models.ReviewUI
import kotlinx.android.synthetic.main.item_review.view.*

class ReviewsListAdapter : RecyclerView.Adapter<ReviewViewHolder>() {

    var reviewList: List<ReviewUI> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        return ReviewViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_review,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(reviewList[position])
    }

    override fun getItemCount(): Int = reviewList.size

    inner class ReviewViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(review: ReviewUI) {
            view.posterTextView.text = review.poster
            view.chapterTextView.text = review.chapter
            view.commentTextView.text = review.comment
        }
    }
}
