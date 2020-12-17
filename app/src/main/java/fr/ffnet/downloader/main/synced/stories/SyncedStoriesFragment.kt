package fr.ffnet.downloader.main.synced.stories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import fr.ffnet.downloader.common.MainApplication
import fr.ffnet.downloader.databinding.FragmentStoriesSyncedBinding
import fr.ffnet.downloader.main.synced.SyncedListAdapter
import fr.ffnet.downloader.main.synced.stories.injection.SyncedStoriesModule
import fr.ffnet.downloader.options.OptionsController
import fr.ffnet.downloader.options.ParentListener
import fr.ffnet.downloader.utils.SwipeToDeleteCallback
import javax.inject.Inject

class SyncedStoriesFragment : Fragment(), ParentListener {

    @Inject lateinit var syncedViewModel: SyncedStoriesViewModel
    @Inject lateinit var optionsController: OptionsController
    @Inject lateinit var picasso: Picasso

    private var _binding: FragmentStoriesSyncedBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentStoriesSyncedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MainApplication.getComponent(requireContext())
            .plus(SyncedStoriesModule(this))
            .inject(this)

        initializeSynced()
        initializeViewModelObservers()
    }

    override fun showErrorMessage(message: String) {
        Snackbar.make(binding.syncedContainer, message, Snackbar.LENGTH_LONG).show()
    }

    private fun initializeSynced() {

        val itemTouchHelper = ItemTouchHelper(object : SwipeToDeleteCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                (binding.syncedResultRecyclerView.adapter as SyncedListAdapter).unsync(
                    viewHolder.bindingAdapterPosition
                )
            }
        })
        itemTouchHelper.attachToRecyclerView(binding.syncedResultRecyclerView)

        binding.syncedResultRecyclerView.adapter = SyncedListAdapter(
            picasso,
            optionsController
        )
    }

    private fun initializeViewModelObservers() {
        syncedViewModel.syncedList.observe(viewLifecycleOwner) { syncedItemList ->
            (binding.syncedResultRecyclerView.adapter as SyncedListAdapter).syncedResultList = syncedItemList
        }
    }
}
