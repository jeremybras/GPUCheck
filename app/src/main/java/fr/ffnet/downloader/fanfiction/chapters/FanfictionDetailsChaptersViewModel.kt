package fr.ffnet.downloader.fanfiction.chapters

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import fr.ffnet.downloader.models.ChapterSyncState
import fr.ffnet.downloader.models.ChapterUI
import fr.ffnet.downloader.repository.DatabaseRepository

class FanfictionDetailsChaptersViewModel(
    private val databaseRepository: DatabaseRepository
) : ViewModel() {

    fun loadChapterList(fanfictionId: String): LiveData<List<ChapterUI>> {
        return Transformations.map(databaseRepository.getChapters(fanfictionId)) { chapterList ->
            chapterList.map {
                ChapterUI(
                    id = it.chapterId,
                    title = it.title,
                    status = if (it.content.isNotEmpty()) ChapterSyncState.SYNCED else ChapterSyncState.UNSYNCED
                )
            }
        }
    }
}
