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
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import fr.ffnet.downloader.R
import fr.ffnet.downloader.common.MainApplication
import fr.ffnet.downloader.main.MainActivity
import fr.ffnet.downloader.main.ViewPagerViewModel
import fr.ffnet.downloader.main.search.JustInViewModel.JustInType
import fr.ffnet.downloader.main.search.injection.SearchModule
import fr.ffnet.downloader.models.JustInUI
import fr.ffnet.downloader.models.SettingType.DEFAULT_SEARCH_ALL
import fr.ffnet.downloader.models.SettingType.DEFAULT_SEARCH_AUTHORS
import fr.ffnet.downloader.models.SettingType.DEFAULT_SEARCH_STORIES
import fr.ffnet.downloader.models.SettingType.JUST_IN_RECENTLY_PUBLISHED
import fr.ffnet.downloader.models.SettingType.JUST_IN_RECENTLY_UPDATED
import fr.ffnet.downloader.models.SettingType.JUST_IN_SHOW_SECTION
import fr.ffnet.downloader.options.OptionsController
import fr.ffnet.downloader.options.ParentListener
import fr.ffnet.downloader.settings.SettingsViewModel
import kotlinx.android.synthetic.main.fragment_search.*
import javax.inject.Inject

class SearchFragment : Fragment(), ParentListener {

    @Inject lateinit var settingsViewModel: SettingsViewModel
    @Inject lateinit var viewPagerViewModel: ViewPagerViewModel
    @Inject lateinit var searchViewModel: SearchViewModel
    @Inject lateinit var justInViewModel: JustInViewModel
    @Inject lateinit var optionsController: OptionsController
    @Inject lateinit var picasso: Picasso

    companion object {
        fun newInstance(): SearchFragment = SearchFragment()

        private const val DISPLAY_JUST_IN_CONTENT = 1
        private const val DISPLAY_JUST_IN_LOADER = 0
    }

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
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        view?.let(::setKeyboardStatusListener)
        return view
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
        searchValidateButton.isEnabled = true
        Snackbar.make(searchContainer, message, Snackbar.LENGTH_LONG).show()
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
                    searchEditText.clearFocus()
                }
            }
        }
    }

    fun onBackPressed() {
        val search = searchEditText.text.toString().isBlank()
        when {
            searchEditText.hasFocus() && search -> transitionToStart()
            searchEditText.hasFocus() -> searchEditText.clearFocus()
            welcomeBlock.isVisible.not() && hasSyncedStories.not() -> transitionToStart()
            else -> {
                transitionToStart()
                (requireActivity() as MainActivity).showSynced()
            }
        }
    }

    private fun initializeSearch() {

        justInRecyclerView.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
        justInRecyclerView.adapter = JustInAdapter(picasso, optionsController)

        searchResultRecyclerView.adapter = SearchListAdapter(
            picasso,
            optionsController
        )

        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                transitionToEnd()
            }
        }
        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                onSearchTriggered()
            }
            true
        }
        searchValidateButton.setOnClickListener {
            if (welcomeBlock.isVisible) {
                transitionToEnd()
            }
            onSearchTriggered()
        }
    }

    private fun initializeViewModelObservers() {

        viewPagerViewModel.hasSyncedStories().observe(viewLifecycleOwner) { hasSyncedStories ->
            this.hasSyncedStories = hasSyncedStories
            welcomeBlock.isVisible = hasSyncedStories.not()
        }

        searchViewModel.searchResult.observe(viewLifecycleOwner) {
            searchValidateButton.isEnabled = true
            (searchResultRecyclerView.adapter as SearchListAdapter).searchResultList = it
        }
        searchViewModel.error.observe(viewLifecycleOwner) {
            showErrorMessage(it)
        }
        searchViewModel.navigateToFanfiction.observe(viewLifecycleOwner) { fanfictionId ->
            searchValidateButton.isEnabled = true
            optionsController.onFetchInformation(fanfictionId)
        }
        searchViewModel.navigateToAuthor.observe(viewLifecycleOwner) {
            Snackbar.make(
                searchContainer,
                "Opening author detail for $it",
                Snackbar.LENGTH_LONG
            ).show()
        }

        justInViewModel.justInList.observe(viewLifecycleOwner) { justInResult ->
            if (justInResult is JustInUI.JustInSuccess) {
                justInViewFlipper.displayedChild = DISPLAY_JUST_IN_CONTENT
                (justInRecyclerView.adapter as JustInAdapter).justInList = justInResult.justInList
            } else {
                justInViewFlipper.displayedChild = DISPLAY_JUST_IN_LOADER
            }
        }

        settingsViewModel.settingList.observe(viewLifecycleOwner) { settingList ->
            settingList.map { setting ->
                when (setting.type) {
                    DEFAULT_SEARCH_ALL -> searchAllRadioButton.isChecked = setting.isEnabled
                    DEFAULT_SEARCH_AUTHORS -> searchAuthorRadioButton.isChecked = setting.isEnabled
                    DEFAULT_SEARCH_STORIES -> searchStoryRadioButton.isChecked = setting.isEnabled

                    JUST_IN_SHOW_SECTION -> justInBlock.isVisible = setting.isEnabled
                    JUST_IN_RECENTLY_PUBLISHED -> if (setting.isEnabled) {
                        justInViewFlipper.displayedChild = DISPLAY_JUST_IN_LOADER
                        justInViewModel.loadJustInList(JustInType.PUBLISHED)
                        justInTextView.text = requireContext().getText(R.string.search_just_in_title_published)
                    }
                    JUST_IN_RECENTLY_UPDATED -> if (setting.isEnabled) {
                        justInViewFlipper.displayedChild = DISPLAY_JUST_IN_LOADER
                        justInViewModel.loadJustInList(JustInType.UPDATED)
                        justInTextView.text = requireContext().getText(R.string.search_just_in_title_updated)
                    }

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
        searchEditText.clearFocus()
        searchValidateButton.isEnabled = false
        val searchText = searchEditText.text.toString()
        val searchType = when (searchTypeRadioGroup.checkedRadioButtonId) {
            R.id.searchAuthorRadioButton -> SearchViewModel.SearchType.SEARCH_AUTHOR
            R.id.searchStoryRadioButton -> SearchViewModel.SearchType.SEARCH_STORY
            else -> SearchViewModel.SearchType.SEARCH_ALL
        }
        searchViewModel.search(searchText, searchType)
    }

    private fun transitionToEnd() {
        welcomeBlock.isVisible = false
        justInBlock.isVisible = false
    }

    private fun transitionToStart() {
        searchEditText.clearFocus()
        welcomeBlock.isVisible = hasSyncedStories.not()
        justInBlock.isVisible = true
    }

    fun shouldShowWelcomeBlock(): Boolean {
        return welcomeBlock.isVisible.not() && hasSyncedStories.not()
    }
}
