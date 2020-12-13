package fr.ffnet.downloader.options

import android.content.res.Resources
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.ffnet.downloader.R
import fr.ffnet.downloader.common.FFLogger
import fr.ffnet.downloader.common.FFLogger.Companion.EVENT_KEY
import fr.ffnet.downloader.repository.AuthorRepository
import fr.ffnet.downloader.repository.DatabaseRepository
import fr.ffnet.downloader.repository.DownloaderRepository
import fr.ffnet.downloader.utils.EpubBuilder
import fr.ffnet.downloader.utils.PdfBuilder
import fr.ffnet.downloader.utils.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OptionsViewModel(
    private val resources: Resources,
    private val dbRepository: DatabaseRepository,
    private val apiRepository: DownloaderRepository,
    private val authorRepository: AuthorRepository,
    private val pdfBuilder: PdfBuilder,
    private val epubBuilder: EpubBuilder
) : ViewModel() {

    val getFile: MutableLiveData<Pair<String, String>> = SingleLiveEvent()
    val navigateToStory: SingleLiveEvent<String> = SingleLiveEvent()
    val navigateToAuthor: SingleLiveEvent<String> = SingleLiveEvent()
    val error: SingleLiveEvent<String> = SingleLiveEvent()

    fun loadFanfictionInfo(fanfictionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (dbRepository.isFanfictionInDatabase(fanfictionId)) {
                FFLogger.d(EVENT_KEY, "Fanfiction $fanfictionId is already in database")
                navigateToStory.postValue(fanfictionId)
                apiRepository.loadFanfictionInfo(fanfictionId)
            } else {
                apiRepository.loadFanfictionInfo(fanfictionId)?.let { story ->
                    FFLogger.d(EVENT_KEY, "Fanfiction $fanfictionId loaded successfully")
                    navigateToStory.postValue(fanfictionId)
                } ?: displayErrorMessage(R.string.load_story_info_fetching_error)
            }
        }
    }

    fun loadAuthorInfo(authorId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val author = authorRepository.getAuthor(authorId)
            if (author != null) {
                navigateToAuthor.postValue(authorId)
                authorRepository.loadProfileInfo(authorId)
            } else {
                authorRepository.loadProfileInfo(authorId)?.let {
                    navigateToAuthor.postValue(authorId)
                } ?: displayErrorMessage(R.string.load_author_info_fetching_error)
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

    fun unsyncAuthor(authorId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            authorRepository.unsyncAuthor(authorId)
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
        error.postValue(errorMessage)
    }
}
