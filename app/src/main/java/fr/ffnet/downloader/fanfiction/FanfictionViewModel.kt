package fr.ffnet.downloader.fanfiction

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo.State.ENQUEUED
import androidx.work.WorkInfo.State.RUNNING
import fr.ffnet.downloader.common.FFLogger
import fr.ffnet.downloader.common.FFLogger.Companion.EVENT_KEY
import fr.ffnet.downloader.fanfiction.FanfictionViewModel.StoryState.DEFAULT
import fr.ffnet.downloader.fanfiction.FanfictionViewModel.StoryState.SYNCED
import fr.ffnet.downloader.fanfiction.FanfictionViewModel.StoryState.SYNCING
import fr.ffnet.downloader.models.SearchUIItem.SearchStoryUI
import fr.ffnet.downloader.repository.DatabaseRepository
import fr.ffnet.downloader.repository.DownloaderRepository
import fr.ffnet.downloader.utils.UIBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FanfictionViewModel(
    private val downloaderRepository: DownloaderRepository,
    private val databaseRepository: DatabaseRepository,
    private val uiBuilder: UIBuilder,
) : ViewModel() {

    private lateinit var isSyncing: LiveData<Boolean>
    private val chapterSyncState: MutableLiveData<ChapterSyncState> = MutableLiveData()

    val globalSyncState: MediatorLiveData<StoryState> by lazy { MediatorLiveData() }

    data class ChapterSyncState(
        val isSynced: Boolean,
        val isInLibrary: Boolean,
    )

    fun loadFanfictionInfo(fanfictionId: String): LiveData<SearchStoryUI> {
        init(fanfictionId)
        return Transformations.map(databaseRepository.getFanfictionInfo(fanfictionId)) {
            val syncState = ChapterSyncState(
                isSynced = it.nbChapters == it.nbSyncedChapters,
                isInLibrary = it.isInLibrary
            )
            FFLogger.d(
                EVENT_KEY,
                "isSynced ${syncState.isSynced} - isInLibrary ${syncState.isInLibrary}"
            )
            chapterSyncState.postValue(syncState)
            uiBuilder.buildSearchStoryUI(it)
        }
    }

    fun syncChapters(fanfictionId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            databaseRepository.addToLibrary(fanfictionId)
            downloaderRepository.downloadChapters(fanfictionId)
        }
    }

    private fun init(fanfictionId: String) {
        isSyncing = Transformations.map(downloaderRepository.getDownloadState(fanfictionId)) { workInfo ->
            val isCurrentlySyncing = workInfo.any { it.state in listOf(ENQUEUED, RUNNING) }
            FFLogger.d(EVENT_KEY, "isSyncing $isCurrentlySyncing")
            isCurrentlySyncing
        }

        globalSyncState.apply {

            removeSource(isSyncing)
            removeSource(chapterSyncState)

            addSource(isSyncing) {
                globalSyncState.value = combineLatestData(
                    isSyncing = isSyncing.value ?: false,
                    chapterSyncState = chapterSyncState.value
                )
            }
            addSource(chapterSyncState) {
                globalSyncState.value = combineLatestData(
                    isSyncing = isSyncing.value ?: false,
                    chapterSyncState = chapterSyncState.value
                )
            }
        }
    }

    private fun combineLatestData(
        isSyncing: Boolean,
        chapterSyncState: ChapterSyncState?,
    ): StoryState {
        return when {
            isSyncing -> SYNCING
            chapterSyncState?.isSynced ?: false -> SYNCED
            else -> DEFAULT
        }
    }

    enum class StoryState {
        DEFAULT, IN_LIBRARY, SYNCING, SYNCED
    }
}
