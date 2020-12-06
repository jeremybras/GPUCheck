package fr.ffnet.downloader.repository

import androidx.lifecycle.LiveData
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import fr.ffnet.downloader.common.FFLogger
import fr.ffnet.downloader.common.FFLogger.Companion.EVENT_KEY

class WorkScheduler(
    private val workManager: WorkManager,
) {

    companion object {
        const val TAG_WORKER = "tag_chapter_worker"
    }

    fun getWorkInfosForUniqueWorkLiveData(
        fanfictionId: String?,
    ): LiveData<List<WorkInfo>> {
        return fanfictionId?.let {
            workManager.getWorkInfosByTagLiveData(it)
        } ?: workManager.getWorkInfosByTagLiveData(TAG_WORKER)
    }

    fun downloadChapter(fanfictionId: String, chapterId: String) {
        FFLogger.d(EVENT_KEY, "Scheduling chapter download for $fanfictionId/$chapterId")
        workManager.enqueueUniqueWork(
            "$fanfictionId-$chapterId",
            ExistingWorkPolicy.KEEP,
            OneTimeWorkRequest.Builder(DownloaderWorker::class.java)
                .addTag(fanfictionId)
                .addTag(TAG_WORKER)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .setInputData(
                    Data.Builder()
                        .putString(DownloaderWorker.FANFICTION_ID_KEY, fanfictionId)
                        .putString(DownloaderWorker.CHAPTER_ID_KEY, chapterId)
                        .build()
                )
                .build()
        )
    }
}
