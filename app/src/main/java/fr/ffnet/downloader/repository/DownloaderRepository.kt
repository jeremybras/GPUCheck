package fr.ffnet.downloader.repository

import androidx.lifecycle.LiveData
import androidx.work.WorkInfo
import fr.ffnet.downloader.common.FFLogger
import fr.ffnet.downloader.common.FFLogger.Companion.EVENT_KEY
import fr.ffnet.downloader.models.Story
import fr.ffnet.downloader.repository.dao.FanfictionDao
import fr.ffnet.downloader.utils.FanfictionBuilder
import fr.ffnet.downloader.utils.FanfictionConverter
import java.io.IOException

class DownloaderRepository(
    private val serviceRegular: RegularCrawlService,
    private val fanfictionBuilder: FanfictionBuilder,
    private val fanfictionDao: FanfictionDao,
    private val converter: FanfictionConverter,
    private val scheduler: WorkScheduler,
) {

    fun loadReviews(fanfictionId: String) {
        val response = serviceRegular.getReviews(fanfictionId).execute()
        if (response.isSuccessful) {
            response.body()?.let { responseBody ->
                val reviewList = fanfictionBuilder.buildReviews(responseBody.string())
                fanfictionDao.removeReviews(fanfictionId)
                fanfictionDao.insertReviewList(converter.toReviewEntity(fanfictionId, reviewList))
            }
        }
    }

    fun getDownloadState(fanfictionId: String? = null): LiveData<List<WorkInfo>> {
        return scheduler.getWorkInfosForUniqueWorkLiveData(fanfictionId)
    }

    fun downloadChapters(fanfictionId: String) {
        val chapterList = fanfictionDao.getChaptersToSync(fanfictionId)

        chapterList.forEach { chapter ->
            scheduler.downloadChapter(fanfictionId, chapter.chapterId)
        }
    }

    fun loadFanfictionInfo(fanfictionId: String): Story? {
        return try {
            FFLogger.d(EVENT_KEY, "Refreshing info for $fanfictionId")
            val response = serviceRegular.getFanfiction(fanfictionId).execute()
            if (response.isSuccessful) {
                response.body()?.let { responseBody ->

                    val existingChapters = fanfictionDao.getChaptersIds(fanfictionId)

                    val (firstChapter, remoteFanfiction) = fanfictionBuilder.buildFanfiction(
                        fanfictionId, responseBody.string()
                    )

                    fanfictionDao.insertFanfiction(
                        converter.toFanfictionEntity(remoteFanfiction)
                    )

                    val chapterList = if (existingChapters.isNotEmpty()) {
                        remoteFanfiction.chapterList.filter { it.id !in existingChapters }
                    } else {
                        remoteFanfiction.chapterList
                    }
                    fanfictionDao.insertChapterList(
                        converter.toChapterEntityList(fanfictionId, chapterList)
                    )
                    fanfictionDao.updateChapter(
                        content = firstChapter,
                        fanfictionId = fanfictionId,
                        chapterId = "1"
                    )
                    remoteFanfiction
                }
            } else null
        } catch (exception: IOException) {
            null
        }
    }

    sealed class FanfictionRepositoryResult {
        data class FanfictionRepositoryResultSuccess(val storyInfo: Story) : FanfictionRepositoryResult()
        object FanfictionRepositoryResultFailure : FanfictionRepositoryResult()
        object FanfictionRepositoryResultServerFailure : FanfictionRepositoryResult()
        object FanfictionRepositoryResultInternetFailure : FanfictionRepositoryResult()
    }
}
