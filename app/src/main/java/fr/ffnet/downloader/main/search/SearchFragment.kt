package fr.ffnet.downloader.main.search

import android.app.Activity
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import fr.ffnet.downloader.R
import fr.ffnet.downloader.common.MainApplication
import fr.ffnet.downloader.databinding.FragmentSearchBinding
import fr.ffnet.downloader.main.MainActivity
import fr.ffnet.downloader.main.ViewPagerViewModel
import fr.ffnet.downloader.main.search.SearchViewModel.SearchType
import fr.ffnet.downloader.main.search.injection.SearchModule
import fr.ffnet.downloader.models.JustInUI
import fr.ffnet.downloader.models.SettingType.DEFAULT_SEARCH_AUTHORS
import fr.ffnet.downloader.models.SettingType.DEFAULT_SEARCH_STORIES
import fr.ffnet.downloader.options.OptionsController
import fr.ffnet.downloader.options.ParentListener
import fr.ffnet.downloader.settings.SettingsViewModel
import javax.inject.Inject

class SearchFragment : Fragment(), ParentListener {

    @Inject lateinit var settingsViewModel: SettingsViewModel
    @Inject lateinit var viewPagerViewModel: ViewPagerViewModel
    @Inject lateinit var searchViewModel: SearchViewModel
    @Inject lateinit var justInViewModel: JustInViewModel
    @Inject lateinit var optionsController: OptionsController
    @Inject lateinit var picasso: Picasso

    companion object {
        private const val DISPLAY_JUST_IN_LOADER = 0
        private const val DISPLAY_JUST_IN_CONTENT = 1
    }

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    enum class KeyboardStatus {
        CLOSED, OPENED
    }

    private var keyboardStatus = KeyboardStatus.CLOSED
    private var hasSyncedStories = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        setKeyboardStatusListener(binding.root)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MainApplication.getComponent(requireContext())
            .plus(SearchModule(this))
            .inject(this)

        initializeSearch()
        initializeViewModelObservers()
    }

    override fun showErrorMessage(message: String) {
        binding.searchValidateButton.isEnabled = true
        Snackbar.make(binding.searchContainer, message, Snackbar.LENGTH_LONG).show()
    }

    private fun setKeyboardStatusListener(view: View) {
        view.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            view.getWindowVisibleDisplayFrame(rect)
            val screenHeight = view.rootView.height
            val keypadHeight = screenHeight - rect.bottom
            if (keypadHeight > screenHeight * 0.15) {
                if (keyboardStatus == KeyboardStatus.CLOSED) {
                    keyboardStatus = KeyboardStatus.OPENED
                }
            } else {
                if (keyboardStatus == KeyboardStatus.OPENED) {
                    keyboardStatus = KeyboardStatus.CLOSED
                    binding.searchEditText.clearFocus()
                }
            }
        }
    }

    fun onBackPressed() {
        val search = binding.searchEditText.text.toString().isBlank()
        when {
            binding.searchEditText.hasFocus() && search -> transitionToStart()
            binding.searchEditText.hasFocus() -> binding.searchEditText.clearFocus()
            binding.welcomeBlock.isVisible.not() && hasSyncedStories.not() -> transitionToStart()
            else -> {
                transitionToStart()
                (requireActivity() as MainActivity).showSynced()
            }
        }
    }

    private fun initializeSearch() {

        binding.justInRecyclerView.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.justInRecyclerView.adapter = JustInAdapter(picasso, optionsController)

        justInViewModel.loadJustInList(JustInViewModel.JustInType.UPDATED)
        binding.justInTextView.text = requireContext().getText(R.string.search_just_in_title_updated)

        binding.searchResultRecyclerView.adapter = SearchListAdapter(
            picasso,
            optionsController
        )

        binding.searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                transitionToEnd()
            }
        }
        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                onSearchTriggered()
            }
            true
        }
        binding.searchValidateButton.setOnClickListener {
            if (binding.welcomeBlock.isVisible) {
                transitionToEnd()
            }
            onSearchTriggered()
        }
    }

    private fun initializeViewModelObservers() {

        viewPagerViewModel.hasSyncedItems().observe(viewLifecycleOwner) { hasSyncedStories ->
            this.hasSyncedStories = hasSyncedStories
            binding.welcomeBlock.isVisible = hasSyncedStories.not()
        }

        searchViewModel.storyResult.observe(viewLifecycleOwner) {
            binding.searchValidateButton.isEnabled = true
            (binding.searchResultRecyclerView.adapter as SearchListAdapter).searchResultList = it
        }
        searchViewModel.authorResult.observe(viewLifecycleOwner) {
            binding.searchValidateButton.isEnabled = true
            (binding.searchResultRecyclerView.adapter as SearchListAdapter).searchResultList = it
        }

        searchViewModel.error.observe(viewLifecycleOwner) {
            showErrorMessage(it)
        }
        searchViewModel.navigateToFanfiction.observe(viewLifecycleOwner) { fanfictionId ->
            binding.searchValidateButton.isEnabled = true
            optionsController.onFetchInformation(fanfictionId)
        }
        searchViewModel.navigateToAuthor.observe(viewLifecycleOwner) {
            showErrorMessage(it)
        }

        justInViewModel.justInList.observe(viewLifecycleOwner) { justInResult ->
            if (justInResult is JustInUI.JustInSuccess) {
                binding.justInViewFlipper.displayedChild = DISPLAY_JUST_IN_CONTENT
                (binding.justInRecyclerView.adapter as JustInAdapter).justInList = justInResult.justInList
            } else {
                binding.justInViewFlipper.displayedChild = DISPLAY_JUST_IN_LOADER
            }
        }

        settingsViewModel.settingList.observe(viewLifecycleOwner) { settingList ->
            settingList.map { setting ->
                when (setting.type) {
                    DEFAULT_SEARCH_AUTHORS -> binding.searchAuthorRadioButton.isChecked = setting.isEnabled
                    DEFAULT_SEARCH_STORIES -> binding.searchStoryRadioButton.isChecked = setting.isEnabled

                    else -> {
                        // Do nothing
                    }
                }
            }
        }
    }

    private fun onSearchTriggered() {
        (requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
            view?.windowToken,
            0
        )
        binding.searchEditText.clearFocus()
        binding.searchValidateButton.isEnabled = false
        val searchText = binding.searchEditText.text.toString()
        val searchType = when (binding.searchTypeRadioGroup.checkedRadioButtonId) {
            R.id.searchAuthorRadioButton -> SearchType.SEARCH_AUTHOR
            else -> SearchType.SEARCH_STORY
        }
        searchViewModel.search(searchText, searchType)
    }

    private fun transitionToEnd() {
        binding.welcomeBlock.isVisible = false
    }

    private fun transitionToStart() {
        binding.searchEditText.clearFocus()
        binding.welcomeBlock.isVisible = hasSyncedStories.not()
    }

    fun shouldShowWelcomeBlock(): Boolean {
        return binding.welcomeBlock.isVisible.not() && hasSyncedStories.not()
    }
}
