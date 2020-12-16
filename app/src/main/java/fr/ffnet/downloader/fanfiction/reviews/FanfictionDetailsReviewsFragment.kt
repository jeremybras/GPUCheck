package fr.ffnet.downloader.fanfiction.reviews

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import fr.ffnet.downloader.common.MainApplication
import fr.ffnet.downloader.databinding.FragmentFanfictionDetailsReviewsBinding
import fr.ffnet.downloader.fanfiction.reviews.injection.FanfictionDetailsReviewsModule
import javax.inject.Inject

class FanfictionDetailsReviewsFragment : Fragment() {

    companion object {
        private const val EXTRA_FANFICTION_ID = "fanfictionId"

        fun newInstance(fanfictionId: String): FanfictionDetailsReviewsFragment {
            return FanfictionDetailsReviewsFragment().apply {
                arguments = Bundle().apply {
                    putString(EXTRA_FANFICTION_ID, fanfictionId)
                }
            }
        }
    }

    @Inject lateinit var viewModel: FanfictionDetailsReviewsViewModel

    private val fanfictionId by lazy { arguments?.getString(EXTRA_FANFICTION_ID) ?: "" }

    private var _binding: FragmentFanfictionDetailsReviewsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFanfictionDetailsReviewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MainApplication.getComponent(requireContext())
            .plus(FanfictionDetailsReviewsModule(this))
            .inject(this)

        binding.reviewsRecyclerView.adapter = ReviewsListAdapter()
        viewModel.loadReviews(fanfictionId).observe(viewLifecycleOwner) { reviewList ->
            (binding.reviewsRecyclerView.adapter as ReviewsListAdapter).reviewList = reviewList
        }
    }
}
