package fr.ffnet.downloader.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import fr.ffnet.downloader.models.Story
import fr.ffnet.downloader.repository.AuthorRepository
import fr.ffnet.downloader.repository.DatabaseRepository
import fr.ffnet.downloader.repository.entities.Author

class ViewPagerViewModel(
    databaseRepository: DatabaseRepository,
    authorRepository: AuthorRepository
) : ViewModel() {

    private var storyList: LiveData<List<Story>> = Transformations.map(databaseRepository.getSyncedFanfictions()) { it }
    private var authorList: LiveData<List<Author>> = Transformations.map(authorRepository.loadSyncedAuthors()) { it }

    private val hasSyncedItems: MediatorLiveData<Boolean> by lazy { MediatorLiveData<Boolean>() }

    fun hasSyncedItems(): LiveData<Boolean> {
        return hasSyncedItems.apply {
            removeSource(storyList)
            removeSource(authorList)
            addSource(storyList) {
                hasSyncedItems.value = storyList.value?.isNotEmpty() ?: false || authorList.value?.isNotEmpty() ?: false
            }
            addSource(authorList) {
                hasSyncedItems.value = storyList.value?.isNotEmpty() ?: false || authorList.value?.isNotEmpty() ?: false
            }
        }
    }
}
