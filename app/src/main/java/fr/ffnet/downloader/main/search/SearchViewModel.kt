package fr.ffnet.downloader.main.search

import android.content.res.Resources
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.ffnet.downloader.R
import fr.ffnet.downloader.common.FFLogger
import fr.ffnet.downloader.common.FFLogger.Companion.EVENT_KEY
import fr.ffnet.downloader.models.SearchUIItem
import fr.ffnet.downloader.models.SearchUIItem.SearchAuthorUI
import fr.ffnet.downloader.models.SearchUIItem.SearchStoryUI
import fr.ffnet.downloader.repository.DatabaseRepository
import fr.ffnet.downloader.repository.DownloaderRepository
import fr.ffnet.downloader.repository.SearchRepository
import fr.ffnet.downloader.utils.DateFormatter
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
    val searchResult: MediatorLiveData<List<SearchUIItem>> by lazy { MediatorLiveData() }

    private val storyResult: MutableLiveData<List<SearchStoryUI>> = MutableLiveData()
    private val authorResult: MutableLiveData<List<SearchAuthorUI>> = MutableLiveData()

    enum class SearchType {
        SEARCH_ALL, SEARCH_AUTHOR, SEARCH_STORY
    }

    init {
        searchResult.removeSource(authorResult)
        searchResult.removeSource(storyResult)

        searchResult.apply {
            addSource(authorResult) {
                searchResult.value = combineLatestData(
                    authorList = authorResult.value ?: emptyList(),
                    storyList = storyResult.value ?: emptyList()
                )
            }
            addSource(storyResult) {
                searchResult.value = combineLatestData(
                    authorList = authorResult.value ?: emptyList(),
                    storyList = storyResult.value ?: emptyList()
                )
            }
        }
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
                    SearchType.SEARCH_ALL -> {
                        searchAuthor(searchText)
                        searchStory(searchText)
                    }
                    SearchType.SEARCH_AUTHOR -> searchAuthor(searchText)
                    SearchType.SEARCH_STORY -> searchStory(searchText)
                }
            }
        } else {
            searchResult.postValue(emptyList())
        }
    }

    private fun searchAuthor(searchText: String) {
        val searchList = searchRepository.searchAuthor(searchText.split(" ").map { it.trim() })
        if (searchList == null) {
            val errorMessage = resources.getString(R.string.repository_error)
            FFLogger.d(EVENT_KEY, errorMessage)
            error.postValue(errorMessage)
        } else {
            authorResult.postValue(
                searchList.map {
                    uiBuilder.buildSearchAuthorUI(it)
                }
            )
        }
    }

    private fun searchStory(searchText: String) {
        val searchList = searchRepository.searchStory(searchText.split(" ").map { it.trim() })
        if (searchList == null) {
            val errorMessage = resources.getString(R.string.repository_error)
            FFLogger.d(EVENT_KEY, errorMessage)
            error.postValue(errorMessage)
        } else {
            storyResult.postValue(
                searchList.sortedByDescending { it.nbChapters }.map {
                    uiBuilder.buildSearchStoryUI(it)
                }
            )
        }
    }

    private fun combineLatestData(
        authorList: List<SearchAuthorUI>,
        storyList: List<SearchStoryUI>
    ): List<SearchUIItem> {
        if (authorList.isEmpty() && storyList.isEmpty()) {
            return listOf(SearchUIItem.SearchUITitle(resources.getString(R.string.search_no_result)))
        }
        val title = SearchUIItem.SearchUITitle(
            resources.getQuantityString(
                R.plurals.search_result_title,
                authorList.size + storyList.size,
                authorList.size + storyList.size
            )
        )
        return listOf(title).plus(authorList).plus(storyList)
    }
}
