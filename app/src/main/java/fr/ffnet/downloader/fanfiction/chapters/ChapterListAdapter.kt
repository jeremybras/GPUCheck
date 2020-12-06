package fr.ffnet.downloader.fanfiction.chapters

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fr.ffnet.downloader.R
import fr.ffnet.downloader.fanfiction.chapters.ChapterListAdapter.ChapterViewHolder
import fr.ffnet.downloader.models.ChapterSyncState.SYNCED
import fr.ffnet.downloader.models.ChapterUI
import kotlinx.android.synthetic.main.item_chapter.view.*

class ChapterListAdapter : RecyclerView.Adapter<ChapterViewHolder>() {

    var chapterList: List<ChapterUI> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChapterViewHolder {
        return ChapterViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_chapter,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ChapterViewHolder, position: Int) {
        holder.bind(chapterList[position])
    }

    override fun getItemCount(): Int = chapterList.size

    inner class ChapterViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(chapter: ChapterUI) {
            view.chapterTitleTextView.text = chapter.title
            if (chapter.status == SYNCED) {
                view.chapterTitleTextView.setTextColor(view.context.getColor(R.color.ff_blue))
                view.chapterTitleTextView.setTypeface(null, Typeface.BOLD)
            } else {
                view.chapterTitleTextView.setTextColor(view.context.getColor(R.color.ff_grey_darker))
                view.chapterTitleTextView.setTypeface(null, Typeface.NORMAL)
            }
        }
    }
}
