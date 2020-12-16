package fr.ffnet.downloader.main.synced

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import fr.ffnet.downloader.R
import fr.ffnet.downloader.databinding.FragmentSyncedBinding
import fr.ffnet.downloader.main.synced.authors.SyncedAuthorsFragment
import fr.ffnet.downloader.main.synced.stories.SyncedStoriesFragment

class SyncedFragment : Fragment() {

    private var _binding: FragmentSyncedBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentSyncedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.syncedViewPager.adapter = SyncedTabAdapter(
            parentFragmentManager,
            mapOf(
                getString(R.string.synced_stories_title) to SyncedStoriesFragment(),
                getString(R.string.synced_authors_title) to SyncedAuthorsFragment(),
            )
        )
        binding.syncedTabLayout.setupWithViewPager(binding.syncedViewPager)
    }
}
