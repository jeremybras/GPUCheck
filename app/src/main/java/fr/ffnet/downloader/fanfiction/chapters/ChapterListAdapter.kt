package fr.ffnet.downloader.fanfiction.chapters

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fr.ffnet.downloader.R
import fr.ffnet.downloader.databinding.ItemChapterBinding
import fr.ffnet.downloader.fanfiction.chapters.ChapterListAdapter.ChapterViewHolder
import fr.ffnet.downloader.models.ChapterSyncState.SYNCED
import fr.ffnet.downloader.models.ChapterUI

class ChapterListAdapter : RecyclerView.Adapter<ChapterViewHolder>() {

    var chapterList: List<ChapterUI> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChapterViewHolder {
        val binding = ItemChapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChapterViewHolder(binding, parent.context)
    }

    override fun onBindViewHolder(holder: ChapterViewHolder, position: Int) {
        holder.bind(chapterList[position])
    }

    override fun getItemCount(): Int = chapterList.size

    inner class ChapterViewHolder(
        private val binding: ItemChapterBinding,
        private val context: Context
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(chapter: ChapterUI) {
            binding.chapterTitleTextView.text = chapter.title
            if (chapter.status == SYNCED) {
                binding.chapterTitleTextView.setTextColor(context.getColor(R.color.ff_blue))
                binding.chapterTitleTextView.setTypeface(null, Typeface.BOLD)
            } else {
                binding.chapterTitleTextView.setTextColor(context.getColor(R.color.ff_grey_darker))
                binding.chapterTitleTextView.setTypeface(null, Typeface.NORMAL)
            }
        }
    }
}
