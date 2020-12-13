package fr.ffnet.downloader.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import fr.ffnet.downloader.models.Story
import fr.ffnet.downloader.models.Review
import fr.ffnet.downloader.repository.AuthorRepository.Companion.PROFILE_TYPE_FAVORITE
import fr.ffnet.downloader.repository.AuthorRepository.Companion.PROFILE_TYPE_MY_STORY
import fr.ffnet.downloader.repository.dao.FanfictionDao
import fr.ffnet.downloader.repository.entities.ChapterEntity
import fr.ffnet.downloader.repository.entities.FanfictionEntity
import fr.ffnet.downloader.utils.FanfictionConverter

class DatabaseRepository(
    private val dao: FanfictionDao,
    private val converter: FanfictionConverter
) {

    fun getFanfictionInfo(fanfictionId: String): LiveData<Story> {
        return Transformations.map(dao.getFanfictionLiveData(fanfictionId)) {
            converter.toFanfiction(it)
        }
    }

    fun getSyncedFanfictions(): LiveData<List<Story>> = transformEntityToModel(
        dao.getSyncedFanfictions()
    )

    fun getChapters(fanfictionId: String): LiveData<List<ChapterEntity>> = dao.getChaptersLivedata(
        fanfictionId
    )

    fun getFavoriteFanfictions(authorId: String): LiveData<List<Story>> {
        return transformEntityToModel(
            dao.getFanfictionsFromAssociatedProfileLiveData(authorId, PROFILE_TYPE_FAVORITE)
        )
    }

    fun getStories(authorId: String): LiveData<List<Story>> {
        return transformEntityToModel(
            dao.getFanfictionsFromAssociatedProfileLiveData(authorId, PROFILE_TYPE_MY_STORY)
        )
    }

    fun getCompleteFanfiction(fanfictionId: String): Story? {
        val fanfiction = dao.getFanfiction(fanfictionId)
        val chapterList = dao.getSyncedChapters(fanfictionId)
        return if (chapterList.isNotEmpty() && fanfiction != null) {
            converter.toFanfiction(fanfiction, chapterList)
        } else null
    }

    fun isFanfictionInDatabase(fanfictionId: String): Boolean {
        return dao.getFanfiction(fanfictionId) != null
    }

    private fun transformEntityToModel(
        liveData: LiveData<List<FanfictionEntity>>
    ): LiveData<List<Story>> = Transformations.map(liveData) { fanfictionList ->
        fanfictionList.map { converter.toFanfiction(it) }
    }

    fun loadReviews(fanfictionId: String): LiveData<List<Review>> {
        return Transformations.map(dao.getReviewsLiveData(fanfictionId)) { reviewEntityList ->
            reviewEntityList.map {
                converter.toReview(it)
            }
        }
    }

    fun addToLibrary(fanfictionId: String) {
        dao.setIsInLibrary(fanfictionId, true)
    }

    fun unsyncFanfiction(fanfictionId: String) {
        dao.unsyncFanfiction(fanfictionId)
        dao.setIsInLibrary(fanfictionId, false)
    }
}
