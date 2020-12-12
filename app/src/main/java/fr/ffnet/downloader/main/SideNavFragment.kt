package fr.ffnet.downloader.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import fr.ffnet.downloader.R
import kotlinx.android.synthetic.main.fragment_side_nav.*

class SideNavFragment : Fragment() {

    enum class DrawerMenuItem {
        SETTINGS
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_side_nav, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        settingsTextView.setOnClickListener {
            (requireActivity() as MainActivity).onMenuItemClicked(DrawerMenuItem.SETTINGS)
        }
    }
}
