package fr.ffnet.downloader.main.synced.stories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.work.WorkInfo
import fr.ffnet.downloader.fanfiction.FanfictionViewModel.StoryState
import fr.ffnet.downloader.models.Setting
import fr.ffnet.downloader.models.SettingType
import fr.ffnet.downloader.models.Story
import fr.ffnet.downloader.models.SyncedUIItem
import fr.ffnet.downloader.repository.DatabaseRepository
import fr.ffnet.downloader.repository.DownloaderRepository
import fr.ffnet.downloader.repository.SettingsRepository
import fr.ffnet.downloader.repository.WorkScheduler
import fr.ffnet.downloader.utils.UIBuilder

class SyncedStoriesViewModel(
    databaseRepository: DatabaseRepository,
    downloaderRepository: DownloaderRepository,
    settingsRepository: SettingsRepository,
    private val uiBuilder: UIBuilder,
) : ViewModel() {

    private var isSyncing: LiveData<List<String>>
    private var storyList: LiveData<List<Story>>
    private var settingList: LiveData<List<Setting>>

    val syncedList: MediatorLiveData<List<SyncedUIItem>> by lazy { MediatorLiveData<List<SyncedUIItem>>() }

    init {
        isSyncing = Transformations.map(downloaderRepository.getDownloadState()) { workInfos ->
            workInfos
                .asSequence()
                .filter { it.tags.contains(WorkScheduler.TAG_WORKER) }
                .filter { it.state in listOf(WorkInfo.State.ENQUEUED, WorkInfo.State.RUNNING) }
                .map { it.tags }
                .flatten()
                .distinct()
                .toList()
        }

        storyList = Transformations.map(databaseRepository.getSyncedFanfictions()) { it }
        settingList = Transformations.map(settingsRepository.getAllSettings()) { it }

        syncedList.apply {

            removeSource(isSyncing)
            removeSource(storyList)
            removeSource(settingList)

            addSource(isSyncing) {
                combineLatestData()
            }
            addSource(storyList) {
                combineLatestData()
            }
            addSource(settingList) {
                combineLatestData()
            }
        }
    }

    private fun combineLatestData() {

        val isSyncing = isSyncing.value ?: emptyList()
        val fanfictionList = storyList.value ?: emptyList()
        val settingList = settingList.value ?: emptyList()

        val shouldShowExportPdf = settingList
            .firstOrNull { it.type == SettingType.PDF_EXPORT }
            ?.isEnabled
            ?: true
        val shouldShowExportEpub = settingList
            .firstOrNull { it.type == SettingType.EPUB_EXPORT }
            ?.isEnabled
            ?: true

        syncedList.value = if (fanfictionList.isEmpty()) {
            emptyList()
        } else {
            val firstItem = uiBuilder.buildSyncedStorySpotlightUI(
                story = fanfictionList.first(),
                storyState = buildStoryState(fanfictionList.first(), isSyncing),
                shouldShowExportPdf = shouldShowExportPdf,
                shouldShowExportEpub = shouldShowExportEpub
            )
            val otherItems = if (fanfictionList.size > 1) {

                fanfictionList.drop(1).map { story ->
                    uiBuilder.buildSyncedStoryUI(
                        story = story,
                        storyState = buildStoryState(story, isSyncing),
                        shouldShowExportPdf = shouldShowExportPdf,
                        shouldShowExportEpub = shouldShowExportEpub
                    )
                }
            } else emptyList()

            listOf(firstItem).plus(otherItems)
        }
    }

    private fun buildStoryState(story: Story, isSyncing: List<String>): StoryState {
        val isSynced = story.nbChapters == story.nbSyncedChapters
        val isCurrentlySyncing = isSyncing.contains(story.id)
        return when {
            isCurrentlySyncing -> StoryState.Syncing
            isSynced -> StoryState.Synced
            else -> StoryState.Default
        }
    }
}
