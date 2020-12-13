package fr.ffnet.downloader.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class MainAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(
    fragmentManager,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) {

    var baseId: Long = 0
    var fragmentList: MutableList<Fragment> = emptyList<Fragment>().toMutableList()
        set(value) {
            field = value
            baseId += count + 2
            notifyDataSetChanged()
        }

    override fun getItem(position: Int): Fragment = fragmentList[position]

    override fun getCount(): Int = fragmentList.size

}
