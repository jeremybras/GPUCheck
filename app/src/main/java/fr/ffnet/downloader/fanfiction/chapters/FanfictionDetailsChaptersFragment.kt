package fr.ffnet.downloader.fanfiction.chapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import fr.ffnet.downloader.common.MainApplication
import fr.ffnet.downloader.databinding.FragmentFanfictionDetailsChaptersBinding
import fr.ffnet.downloader.fanfiction.chapters.injection.FanfictionDetailsChaptersModule
import javax.inject.Inject

class FanfictionDetailsChaptersFragment : Fragment() {

    companion object {
        private const val EXTRA_FANFICTION_ID = "fanfictionId"

        fun newInstance(fanfictionId: String): FanfictionDetailsChaptersFragment {
            return FanfictionDetailsChaptersFragment().apply {
                arguments = Bundle().apply {
                    putString(EXTRA_FANFICTION_ID, fanfictionId)
                }
            }
        }
    }

    @Inject lateinit var viewModel: FanfictionDetailsChaptersViewModel

    private val fanfictionId by lazy { arguments?.getString(EXTRA_FANFICTION_ID) ?: "" }

    private var _binding: FragmentFanfictionDetailsChaptersBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFanfictionDetailsChaptersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MainApplication.getComponent(requireContext())
            .plus(FanfictionDetailsChaptersModule(this))
            .inject(this)

        binding.chaptersRecyclerView.adapter = ChapterListAdapter()
        viewModel.loadChapterList(fanfictionId).observe(this.viewLifecycleOwner) { chapterList ->
            (binding.chaptersRecyclerView.adapter as ChapterListAdapter).chapterList = chapterList
        }
    }
}
