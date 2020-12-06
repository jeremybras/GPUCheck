package fr.ffnet.downloader.fanfiction.reviews

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import fr.ffnet.downloader.R
import fr.ffnet.downloader.common.MainApplication
import fr.ffnet.downloader.fanfiction.reviews.injection.FanfictionDetailsReviewsModule
import kotlinx.android.synthetic.main.fragment_fanfiction_details_reviews.*
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_fanfiction_details_reviews, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MainApplication.getComponent(requireContext())
            .plus(FanfictionDetailsReviewsModule(this))
            .inject(this)

        reviewsRecyclerView.adapter = ReviewsListAdapter()
        viewModel.loadReviews(fanfictionId).observe(viewLifecycleOwner, { reviewList ->
            (reviewsRecyclerView.adapter as ReviewsListAdapter).reviewList = reviewList
        })
    }
}
