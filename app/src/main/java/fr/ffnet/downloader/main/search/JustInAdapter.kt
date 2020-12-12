package fr.ffnet.downloader.main.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import fr.ffnet.downloader.R
import fr.ffnet.downloader.models.JustInUI.JustInUIItem
import fr.ffnet.downloader.options.OnFanfictionActionsListener
import kotlinx.android.synthetic.main.item_just_in.view.*

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
        return JustInViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_just_in,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: JustInViewHolder, position: Int) {
        holder.bind(justInList[position])
    }

    override fun getItemCount(): Int = justInList.size

    inner class JustInViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(justInUI: JustInUIItem) {

            view.setOnClickListener {
                actionsListener.onFetchInformation(justInUI.storyId)
            }

            picasso
                .load(justInUI.imageUrl)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(view.justInImageView)
        }
    }
}
