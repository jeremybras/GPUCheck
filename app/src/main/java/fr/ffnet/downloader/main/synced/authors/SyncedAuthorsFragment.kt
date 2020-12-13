package fr.ffnet.downloader.main.synced.authors

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import com.squareup.picasso.Picasso
import fr.ffnet.downloader.R
import fr.ffnet.downloader.common.MainApplication
import fr.ffnet.downloader.main.synced.SyncedListAdapter
import fr.ffnet.downloader.main.synced.authors.injection.SyncedAuthorsModule
import fr.ffnet.downloader.options.OptionsController
import fr.ffnet.downloader.options.ParentListener
import kotlinx.android.synthetic.main.fragment_authors_synced.*
import javax.inject.Inject

class SyncedAuthorsFragment : Fragment(), ParentListener {

    @Inject lateinit var syncedViewModel: SyncedAuthorsViewModel
    @Inject lateinit var optionsController: OptionsController
    @Inject lateinit var picasso: Picasso

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? = inflater.inflate(R.layout.fragment_authors_synced, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MainApplication.getComponent(requireContext())
            .plus(SyncedAuthorsModule(this))
            .inject(this)

        syncedViewModel.loadAuthorsStories()
        initializeSynced()
        setObservers()
    }

    override fun showErrorMessage(message: String) {

    }

    private fun initializeSynced() {
        syncedResultRecyclerView.adapter = SyncedListAdapter(
            picasso,
            optionsController
        )
    }

    private fun setObservers() {
        syncedViewModel.authors.observe(viewLifecycleOwner) {
            (syncedResultRecyclerView.adapter as SyncedListAdapter).syncedResultList = it
        }
    }
}
