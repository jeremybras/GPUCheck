package fr.ffnet.downloader.main.search

import android.content.res.Resources
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.ffnet.downloader.R
import fr.ffnet.downloader.common.FFLogger
import fr.ffnet.downloader.common.FFLogger.Companion.EVENT_KEY
import fr.ffnet.downloader.models.SearchUIItem.SearchAuthorUI
import fr.ffnet.downloader.models.SearchUIItem.SearchStoryUI
import fr.ffnet.downloader.repository.SearchRepository
import fr.ffnet.downloader.utils.SingleLiveEvent
import fr.ffnet.downloader.utils.UIBuilder
import fr.ffnet.downloader.utils.UrlTransformer
import fr.ffnet.downloader.utils.UrlTransformer.UrlTransformationResult.UrlTransformSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchViewModel(
    private val urlTransformer: UrlTransformer,
    private val resources: Resources,
    private val searchRepository: SearchRepository,
    private val uiBuilder: UIBuilder
) : ViewModel() {

    val navigateToFanfiction: SingleLiveEvent<String> = SingleLiveEvent()
    val navigateToAuthor: SingleLiveEvent<String> = SingleLiveEvent()
    val error: SingleLiveEvent<String> = SingleLiveEvent()

    val storyResult: MutableLiveData<List<SearchStoryUI>> = MutableLiveData()
    val authorResult: MutableLiveData<List<SearchAuthorUI>> = MutableLiveData()

    enum class SearchType {
        SEARCH_AUTHOR, SEARCH_STORY
    }

    fun search(searchText: String?, searchType: SearchType) {
        val fanfictionUrlResult = urlTransformer.getFanfictionIdFromUrl(searchText)
        val authorUrlResult = urlTransformer.getProfileIdFromUrl(searchText)

        when {
            fanfictionUrlResult is UrlTransformSuccess -> navigateToFanfiction.postValue(
                fanfictionUrlResult.id
            )
            authorUrlResult is UrlTransformSuccess -> navigateToAuthor.postValue(authorUrlResult.id)
            else -> searchKeywords(searchText, searchType)
        }
    }

    private fun searchKeywords(searchText: String?, searchType: SearchType) {
        FFLogger.d(
            EVENT_KEY,
            "Could not get fanfictionId from url $searchText, trying to search keywords"
        )

        if (searchText != null) {
            viewModelScope.launch(Dispatchers.IO) {

                when (searchType) {
                    SearchType.SEARCH_AUTHOR -> searchAuthor(searchText)
                    SearchType.SEARCH_STORY -> searchStory(searchText)
                }
            }
        } else {
            storyResult.postValue(emptyList())
            authorResult.postValue(emptyList())
        }
    }

    private fun searchAuthor(searchText: String) {
        val searchList = searchRepository.searchAuthor(searchText.split(" ").map { it.trim() })
        if (searchList == null) {
            displayErrorMessage(R.string.repository_error)
        } else {
            storyResult.postValue(emptyList())
            authorResult.postValue(
                searchList.map {
                    uiBuilder.buildSearchAuthorUI(it, R.drawable.ic_add)
                }
            )
        }
    }

    private fun searchStory(searchText: String) {
        val searchList = searchRepository.searchStory(searchText.split(" ").map { it.trim() })
        if (searchList == null) {
            displayErrorMessage(R.string.repository_error)
        } else {
            authorResult.postValue(emptyList())
            storyResult.postValue(
                searchList.sortedByDescending { it.nbChapters }.map {
                    uiBuilder.buildSearchStoryUI(it)
                }
            )
        }
    }

    private fun displayErrorMessage(messageResource: Int) {
        val errorMessage = resources.getString(messageResource)
        FFLogger.d(EVENT_KEY, errorMessage)
        error.postValue(errorMessage)
    }
}
