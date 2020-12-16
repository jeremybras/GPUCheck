package fr.ffnet.downloader.main.search

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import fr.ffnet.downloader.R
import fr.ffnet.downloader.databinding.ItemJustInBinding
import fr.ffnet.downloader.models.JustInUI.JustInUIItem
import fr.ffnet.downloader.options.OnFanfictionActionsListener

class JustInAdapter(
    private val picasso: Picasso,
    private val actionsListener: OnFanfictionActionsListener
) : RecyclerView.Adapter<JustInAdapter.JustInViewHolder>() {

    var justInList: List<JustInUIItem> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JustInViewHolder {
        val binding = ItemJustInBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return JustInViewHolder(binding, parent.context)
    }

    override fun onBindViewHolder(holder: JustInViewHolder, position: Int) {
        holder.bind(justInList[position])
    }

    override fun getItemCount(): Int = justInList.size

    inner class JustInViewHolder(
        private val binding: ItemJustInBinding,
        private val context: Context
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(justInUI: JustInUIItem) {

            binding.root.setOnClickListener {
                actionsListener.onFetchInformation(justInUI.storyId)
            }

            picasso
                .load(justInUI.imageUrl)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(binding.justInImageView)
        }
    }
}
