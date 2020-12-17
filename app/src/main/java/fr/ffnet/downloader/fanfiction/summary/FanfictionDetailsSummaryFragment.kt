package fr.ffnet.downloader.fanfiction.summary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import fr.ffnet.downloader.common.MainApplication
import fr.ffnet.downloader.databinding.FragmentFanfictionDetailsSummaryBinding
import fr.ffnet.downloader.fanfiction.summary.injection.FanfictionDetailsSummaryModule
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

    private var _binding: FragmentFanfictionDetailsSummaryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFanfictionDetailsSummaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MainApplication.getComponent(requireContext())
            .plus(FanfictionDetailsSummaryModule(this))
            .inject(this)

        viewModel.loadStoryUI(fanfictionId).observe(viewLifecycleOwner) { story ->
            if (story.language.isEmpty()) {
                binding.languageButton.isVisible = false
            }
            if (story.genre.isEmpty()) {
                binding.genreButton.isVisible = false
            }
            binding.languageButton.text = story.language
            binding.genreButton.text = story.genre
            binding.chaptersButton.text = story.chaptersNb.toString()
            binding.favoritesButton.text = story.favoritesNb
            binding.reviewsButton.text = story.reviewsNb
            binding.commentTextView.text = story.summary
        }
    }

}
