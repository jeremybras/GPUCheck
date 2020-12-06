package fr.ffnet.downloader.fanfiction.summary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import fr.ffnet.downloader.R
import fr.ffnet.downloader.common.MainApplication
import fr.ffnet.downloader.fanfiction.summary.injection.FanfictionDetailsSummaryModule
import kotlinx.android.synthetic.main.fragment_fanfiction_details_summary.*
import javax.inject.Inject

class FanfictionDetailsSummaryFragment : Fragment() {

    companion object {
        private const val EXTRA_FANFICTION_ID = "fanfictionId"

        fun newInstance(fanfictionId: String): FanfictionDetailsSummaryFragment {
            return FanfictionDetailsSummaryFragment().apply {
                arguments = Bundle().apply {
                    putString(EXTRA_FANFICTION_ID, fanfictionId)
                }
            }
        }
    }

    @Inject lateinit var viewModel: FanfictionDetailsSummaryViewModel

    private val fanfictionId by lazy { arguments?.getString(EXTRA_FANFICTION_ID) ?: "" }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_fanfiction_details_summary, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MainApplication.getComponent(requireContext())
            .plus(FanfictionDetailsSummaryModule(this))
            .inject(this)

        viewModel.loadStoryUI(fanfictionId).observe(viewLifecycleOwner, { story ->
            if (story.language.isEmpty()) {
                languageButton.isVisible = false
            }
            if (story.genre.isEmpty()) {
                genreButton.isVisible = false
            }
            languageButton.text = story.language
            genreButton.text = story.genre
            chaptersButton.text = story.chaptersNb.toString()
            favoritesButton.text = story.favoritesNb
            reviewsButton.text = story.reviewsNb
            commentTextView.text = story.summary
        })
    }

}
