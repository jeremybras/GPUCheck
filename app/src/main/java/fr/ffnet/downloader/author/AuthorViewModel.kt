package fr.ffnet.downloader.author

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.ffnet.downloader.R
import fr.ffnet.downloader.models.SearchUIItem.SearchStoryUI
import fr.ffnet.downloader.models.SyncedUIItem
import fr.ffnet.downloader.repository.AuthorRepository
import fr.ffnet.downloader.repository.DatabaseRepository
import fr.ffnet.downloader.utils.SingleLiveEvent
import fr.ffnet.downloader.utils.UIBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthorViewModel(
    private val resources: Resources,
    private val databaseRepository: DatabaseRepository,
    private val authorRepository: AuthorRepository,
    private val uiBuilder: UIBuilder
) : ViewModel() {

    val error: SingleLiveEvent<String> = SingleLiveEvent()
    val author = MutableLiveData<SyncedUIItem.SyncedAuthorUI>()
    lateinit var storyList: LiveData<List<SearchStoryUI>>

    fun loadAuthorInfo(authorId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val authorResult = authorRepository.getAuthor(authorId)
            if (authorResult != null) {
                author.postValue(
                    uiBuilder.buildAuthorUI(authorResult)
                )
            } else {
                error.postValue(resources.getString(R.string.author_load_error))
            }
        }
        storyList = Transformations.map(databaseRepository.getStories(authorId)) { fanfictionList ->
            fanfictionList.map { uiBuilder.buildSearchStoryUI(it) }
        }
    }
}
