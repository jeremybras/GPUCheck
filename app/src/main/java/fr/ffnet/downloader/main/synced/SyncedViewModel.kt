package fr.ffnet.downloader.main.synced

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.work.WorkInfo
import fr.ffnet.downloader.R
import fr.ffnet.downloader.fanfiction.FanfictionViewModel.StoryState
import fr.ffnet.downloader.models.Fanfiction
import fr.ffnet.downloader.models.SyncedUIItem
import fr.ffnet.downloader.repository.DatabaseRepository
import fr.ffnet.downloader.repository.DownloaderRepository
import fr.ffnet.downloader.repository.WorkScheduler
import fr.ffnet.downloader.utils.UIBuilder

class SyncedViewModel(
    private val resources: Resources,
    databaseRepository: DatabaseRepository,
    downloaderRepository: DownloaderRepository,
    private val uiBuilder: UIBuilder,
) : ViewModel() {

    private var isSyncing: LiveData<List<String>>
    private var fanfictionList: LiveData<List<Fanfiction>>

    val syncedList: MediatorLiveData<List<SyncedUIItem>> by lazy { MediatorLiveData() }

    init {
        isSyncing = Transformations.map(downloaderRepository.getDownloadState()) { workInfos ->
            workInfos
                .asSequence()
                .filter { it.tags.contains(WorkScheduler.TAG_WORKER) }
                .filter { it.state in listOf(WorkInfo.State.ENQUEUED, WorkInfo.State.RUNNING) }
                .flatMap { it.tags }
                .distinct()
                .toList()
        }

        fanfictionList = Transformations.map(databaseRepository.getSyncedFanfictions()) { it }

        syncedList.apply {
            removeSource(isSyncing)
            removeSource(fanfictionList)
            addSource(isSyncing) {
                syncedList.value = combineLatestData(
                    isSyncing = isSyncing.value ?: emptyList(),
                    fanfictionList = fanfictionList.value ?: emptyList()
                )
            }
            addSource(fanfictionList) {
                syncedList.value = combineLatestData(
                    isSyncing = isSyncing.value ?: emptyList(),
                    fanfictionList = fanfictionList.value ?: emptyList()
                )
            }
        }
    }

    private fun combineLatestData(
        isSyncing: List<String>,
        fanfictionList: List<Fanfiction>,
    ): List<SyncedUIItem> {
        return if (fanfictionList.isEmpty()) {
            emptyList()
        } else {
            val title = SyncedUIItem.SyncedUITitle(
                title = resources.getString(R.string.synced_stories_title),
                subtitle = resources.getQuantityString(
                    R.plurals.synced_nb_stories,
                    fanfictionList.size,
                    fanfictionList.size
                )
            )

            val firstItem = uiBuilder.buildSyncedStorySpotlightUI(
                fanfictionList.first(),
                buildStoryState(fanfictionList.first(), isSyncing)
            )
            val otherItems = if (fanfictionList.size > 1) {

                fanfictionList.drop(1).map { fanfiction ->
                    uiBuilder.buildSyncedStoryUI(
                        fanfiction,
                        buildStoryState(fanfiction, isSyncing)
                    )
                }
            } else emptyList()

            listOf(title).plus(firstItem).plus(otherItems)
        }
    }

    private fun buildStoryState(fanfiction: Fanfiction, isSyncing: List<String>): StoryState {
        val isSynced = fanfiction.nbChapters == fanfiction.nbSyncedChapters
        val isCurrentlySyncing = isSyncing.contains(fanfiction.id)
        return when {
            isCurrentlySyncing -> StoryState.SYNCING
            isSynced -> StoryState.SYNCED
            else -> StoryState.DEFAULT
        }
    }
}
