package fr.ffnet.downloader.main.synced

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class SyncedTabAdapter(
    fragmentManager: FragmentManager,
    private val fragmentMap: Map<String, Fragment>
) : FragmentStatePagerAdapter(
    fragmentManager,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) {

    override fun getItem(position: Int): Fragment = fragmentMap.values.elementAt(position)

    override fun getCount(): Int = fragmentMap.size

    override fun getPageTitle(position: Int): CharSequence? = fragmentMap.keys.elementAt(position)
}
