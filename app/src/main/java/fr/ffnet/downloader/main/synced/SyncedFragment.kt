package fr.ffnet.downloader.main.synced

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import fr.ffnet.downloader.R
import fr.ffnet.downloader.main.synced.authors.SyncedAuthorsFragment
import fr.ffnet.downloader.main.synced.stories.SyncedStoriesFragment
import kotlinx.android.synthetic.main.fragment_synced.*

class SyncedFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? = inflater.inflate(R.layout.fragment_synced, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        syncedViewPager.adapter = SyncedTabAdapter(
            parentFragmentManager,
            mapOf(
                getString(R.string.synced_stories_title) to SyncedStoriesFragment(),
                getString(R.string.synced_authors_title) to SyncedAuthorsFragment(),
            )
        )
        syncedTabLayout.setupWithViewPager(syncedViewPager)
    }
}
