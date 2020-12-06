package fr.ffnet.downloader.main.synced

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import fr.ffnet.downloader.R
import fr.ffnet.downloader.common.MainApplication
import fr.ffnet.downloader.main.synced.injection.SyncedModule
import fr.ffnet.downloader.options.OptionsController
import fr.ffnet.downloader.options.ParentListener
import fr.ffnet.downloader.utils.SwipeToDeleteCallback
import kotlinx.android.synthetic.main.fragment_synced.*
import javax.inject.Inject

class SyncedFragment : Fragment(), ParentListener {

    @Inject lateinit var syncedViewModel: SyncedViewModel
    @Inject lateinit var optionsController: OptionsController
    @Inject lateinit var picasso: Picasso

    companion object {
        fun newInstance(): SyncedFragment = SyncedFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? = inflater.inflate(R.layout.fragment_synced, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MainApplication.getComponent(requireContext())
            .plus(SyncedModule(this))
            .inject(this)

        initializeSynced()
        initializeViewModelObservers()
    }

    override fun showErrorMessage(message: String) {
        Snackbar.make(syncedContainer, message, Snackbar.LENGTH_LONG).show()
    }

    private fun initializeSynced() {

        val itemTouchHelper = ItemTouchHelper(object : SwipeToDeleteCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                (syncedResultRecyclerView.adapter as SyncedListAdapter).unsync(
                    viewHolder.bindingAdapterPosition
                )
            }
        })
        itemTouchHelper.attachToRecyclerView(syncedResultRecyclerView)

        syncedResultRecyclerView.adapter = SyncedListAdapter(
            picasso,
            optionsController
        )
    }

    private fun initializeViewModelObservers() {
        syncedViewModel.syncedList.observe(viewLifecycleOwner, { syncedItemList ->
            (syncedResultRecyclerView.adapter as SyncedListAdapter).syncedResultList = syncedItemList
        })
    }
}
