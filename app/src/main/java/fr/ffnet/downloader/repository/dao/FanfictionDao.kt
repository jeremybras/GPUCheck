package fr.ffnet.downloader.repository.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import fr.ffnet.downloader.repository.entities.ChapterEntity
import fr.ffnet.downloader.repository.entities.FanfictionEntity
import fr.ffnet.downloader.repository.entities.ReviewEntity

@Dao
interface FanfictionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFanfiction(fanfiction: FanfictionEntity)

    @Insert
    fun insertChapterList(chapterList: List<ChapterEntity>)

    @Query("UPDATE ChapterEntity SET content = :content WHERE fanfictionId = :fanfictionId AND chapterId = :chapterId")
    fun updateChapter(content: String, fanfictionId: String, chapterId: String)

    @Query("UPDATE ChapterEntity SET content = '' WHERE fanfictionId = :fanfictionId")
    fun unsyncFanfiction(fanfictionId: String): Int

    @Query("UPDATE FanfictionEntity SET isInLibrary = :isInLibrary WHERE id = :fanfictionId")
    fun setIsInLibrary(fanfictionId: String, isInLibrary: Boolean)

    @Query(
        "SELECT " +
            "FanfictionEntity.*, " +
            "(SELECT COUNT(*) FROM ChapterEntity WHERE FanfictionEntity.id = fanfictionId) AS nbChapters, " +
            "(SELECT COUNT(*) FROM ChapterEntity WHERE FanfictionEntity.id = fanfictionId AND content != '') AS nbSyncedChapters " +
            "FROM FanfictionEntity WHERE id = :fanfictionId"
    )
    fun getFanfiction(fanfictionId: String): FanfictionEntity?

    @Query(
        "SELECT " +
            "FanfictionEntity.*, " +
            "(SELECT COUNT(*) FROM ChapterEntity WHERE FanfictionEntity.id = fanfictionId) AS nbChapters, " +
            "(SELECT COUNT(*) FROM ChapterEntity WHERE FanfictionEntity.id = fanfictionId AND content != '') AS nbSyncedChapters " +
            "FROM FanfictionEntity WHERE id = :fanfictionId"
    )
    fun getFanfictionLiveData(fanfictionId: String): LiveData<FanfictionEntity>

    @Query(
        "SELECT " +
            "FanfictionEntity.*, " +
            "(SELECT COUNT(*) FROM ChapterEntity WHERE FanfictionEntity.id = fanfictionId) AS nbChapters, " +
            "(SELECT COUNT(*) FROM ChapterEntity WHERE FanfictionEntity.id = fanfictionId AND content != '') AS nbSyncedChapters " +
            "FROM FanfictionEntity " +
            "WHERE id IN (SELECT fanfictionId FROM ChapterEntity WHERE content != '' GROUP BY fanfictionId)"
    )
    fun getSyncedFanfictions(): LiveData<List<FanfictionEntity>>

    @Query(
        "SELECT " +
            "FanfictionEntity.*, " +
            "(SELECT COUNT(*) FROM ChapterEntity WHERE FanfictionEntity.id = fanfictionId) AS nbChapters, " +
            "(SELECT COUNT(*) FROM ChapterEntity WHERE FanfictionEntity.id = fanfictionId AND content != '') AS nbSyncedChapters " +
            "FROM FanfictionEntity WHERE fetchedDate IS NOT NULL"
    )
    fun getFanfictionHistory(): LiveData<List<FanfictionEntity>>

    @Query("SELECT * FROM ChapterEntity WHERE fanfictionId = :fanfictionId")
    fun getChaptersLivedata(fanfictionId: String): LiveData<List<ChapterEntity>>

    @Query("SELECT * FROM ChapterEntity WHERE fanfictionId = :fanfictionId AND content = ''")
    fun getChaptersToSync(fanfictionId: String): List<ChapterEntity>

    @Query("SELECT * FROM ChapterEntity WHERE fanfictionId = :fanfictionId AND content != ''")
    fun getSyncedChapters(fanfictionId: String): List<ChapterEntity>

    @Query("SELECT chapterId FROM ChapterEntity WHERE fanfictionId = :fanfictionId")
    fun getChaptersIds(fanfictionId: String): List<String>

    @Query(
        "SELECT " +
            "FanfictionEntity.*, ProfileFanfictionEntity.profileType, " +
            "CASE WHEN ((SELECT COUNT(*) FROM ChapterEntity WHERE FanfictionEntity.id = fanfictionId)) > 0 THEN ((SELECT COUNT(*) FROM ChapterEntity WHERE FanfictionEntity.id = fanfictionId)) ELSE nbChapters END AS nbChapters, " +
            "(SELECT COUNT(*) FROM ChapterEntity WHERE FanfictionEntity.id = fanfictionId AND content != '') AS nbSyncedChapters " +
            "FROM FanfictionEntity " +
            "LEFT JOIN ProfileFanfictionEntity ON (ProfileFanfictionEntity.fanfictionId = FanfictionEntity.id) " +
            "LEFT JOIN AuthorEntity ON (ProfileFanfictionEntity.profileId = AuthorEntity.authorId) " +
            "WHERE ProfileFanfictionEntity.profileType = :profileType " +
            "AND ProfileFanfictionEntity.profileId = :authorId " +
            "ORDER BY FanfictionEntity.fetchedDate DESC, FanfictionEntity.updatedDate DESC"
    )
    fun getFanfictionsFromAssociatedProfileLiveData(
        authorId: String,
        profileType: Int
    ): LiveData<List<FanfictionEntity>>


    // Reviews
    @Query("DELETE FROM ReviewEntity WHERE fanfictionId = :fanfictionId")
    fun removeReviews(fanfictionId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReviewList(reviewEntityList: List<ReviewEntity>)

    @Query("SELECT * FROM ReviewEntity WHERE fanfictionId = :fanfictionId")
    fun getReviewsLiveData(fanfictionId: String): LiveData<List<ReviewEntity>>
}
