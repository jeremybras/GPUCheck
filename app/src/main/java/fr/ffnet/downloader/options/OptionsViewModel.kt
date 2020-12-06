package fr.ffnet.downloader.options

import android.content.res.Resources
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.ffnet.downloader.R
import fr.ffnet.downloader.common.FFLogger
import fr.ffnet.downloader.common.FFLogger.Companion.EVENT_KEY
import fr.ffnet.downloader.repository.DatabaseRepository
import fr.ffnet.downloader.repository.DownloaderRepository
import fr.ffnet.downloader.repository.DownloaderRepository.FanfictionRepositoryResult.FanfictionRepositoryResultFailure
import fr.ffnet.downloader.repository.DownloaderRepository.FanfictionRepositoryResult.FanfictionRepositoryResultInternetFailure
import fr.ffnet.downloader.repository.DownloaderRepository.FanfictionRepositoryResult.FanfictionRepositoryResultServerFailure
import fr.ffnet.downloader.repository.DownloaderRepository.FanfictionRepositoryResult.FanfictionRepositoryResultSuccess
import fr.ffnet.downloader.utils.EpubBuilder
import fr.ffnet.downloader.utils.PdfBuilder
import fr.ffnet.downloader.utils.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OptionsViewModel(
    private val resources: Resources,
    private val dbRepository: DatabaseRepository,
    private val apiRepository: DownloaderRepository,
    private val pdfBuilder: PdfBuilder,
    private val epubBuilder: EpubBuilder
) : ViewModel() {

    val getFile: MutableLiveData<Pair<String, String>> = SingleLiveEvent()
    val navigateToFanfiction: SingleLiveEvent<String> = SingleLiveEvent()
    val error: SingleLiveEvent<SearchError> = SingleLiveEvent()

    fun loadFanfictionInfo(fanfictionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val isFanfictionInDatabase = dbRepository.isFanfictionInDatabase(fanfictionId)
            if (isFanfictionInDatabase) {
                FFLogger.d(EVENT_KEY, "Fanfiction $fanfictionId is already in database")
                navigateToFanfiction.postValue(fanfictionId)
                apiRepository.loadFanfictionInfo(fanfictionId)
            } else {
                when (apiRepository.loadFanfictionInfo(fanfictionId)) {
                    is FanfictionRepositoryResultSuccess -> {
                        FFLogger.d(EVENT_KEY, "Fanfiction $fanfictionId loaded successfully")
                        navigateToFanfiction.postValue(fanfictionId)
                    }
                    FanfictionRepositoryResultFailure,
                    FanfictionRepositoryResultServerFailure,
                    FanfictionRepositoryResultInternetFailure -> displayErrorMessage(R.string.load_story_info_fetching_error)
                }
            }
        }
    }

    fun onSyncFanfiction(fanfictionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            dbRepository.addToLibrary(fanfictionId)
            apiRepository.downloadChapters(fanfictionId)
        }
    }

    fun unsyncFanfiction(fanfictionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            dbRepository.unsyncFanfiction(fanfictionId)
        }
    }

    fun buildPdf(absolutePath: String, fanfictionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            dbRepository.getCompleteFanfiction(fanfictionId)?.let { fanfiction ->
                val fileName = pdfBuilder.buildPdf(absolutePath, fanfiction)
                getFile.postValue(fileName to absolutePath)
            }
        }
    }

    fun buildEpub(absolutePath: String, fanfictionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            dbRepository.getCompleteFanfiction(fanfictionId)?.let { fanfiction ->
                val fileName = epubBuilder.buildEpub(absolutePath, fanfiction)
                getFile.postValue(fileName to absolutePath)
            }
        }
    }

    private fun displayErrorMessage(messageResource: Int) {
        val errorMessage = resources.getString(messageResource)
        FFLogger.d(EVENT_KEY, errorMessage)
        error.postValue(
            SearchError.InfoFetchingFailed(errorMessage)
        )
    }

    sealed class SearchError(val message: String) {
        data class InfoFetchingFailed(val msg: String) : SearchError(msg)
    }
}
