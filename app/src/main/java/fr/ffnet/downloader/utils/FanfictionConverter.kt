package fr.ffnet.downloader.utils

import fr.ffnet.downloader.models.Chapter
import fr.ffnet.downloader.models.Story
import fr.ffnet.downloader.models.Review
import fr.ffnet.downloader.repository.entities.ChapterEntity
import fr.ffnet.downloader.repository.entities.FanfictionEntity
import fr.ffnet.downloader.repository.entities.ReviewEntity
import javax.inject.Inject

class FanfictionConverter @Inject constructor() {

    fun toFanfiction(
        fanfiction: FanfictionEntity,
        chapterList: List<ChapterEntity> = emptyList()
    ) = Story(
        id = fanfiction.id,
        title = fanfiction.title,
        isInLibrary = fanfiction.isInLibrary,
        image = fanfiction.image,
        words = fanfiction.words,
        author = fanfiction.author,
        summary = fanfiction.summary,
        language = fanfiction.language,
        category = fanfiction.category,
        genre = fanfiction.genre,
        nbReviews = fanfiction.nbReviews,
        nbFavorites = fanfiction.nbFavorites,
        publishedDate = fanfiction.publishedDate,
        updatedDate = fanfiction.updatedDate,
        fetchedDate = fanfiction.fetchedDate,
        profileType = fanfiction.profileType,
        nbChapters = fanfiction.nbChapters,
        nbSyncedChapters = fanfiction.nbSyncedChapters,
        chapterList = chapterList.map { toChapter(it) }
    )

    private fun toChapter(chapter: ChapterEntity): Chapter = Chapter(
        id = chapter.chapterId,
        title = chapter.title,
        content = chapter.content
    )

    fun toFanfictionEntity(story: Story): FanfictionEntity = FanfictionEntity(
        id = story.id,
        title = story.title,
        isInLibrary = false,
        image = story.image,
        words = story.words,
        author = story.author,
        summary = story.summary,
        language = story.language,
        category = story.category,
        genre = story.genre,
        nbReviews = story.nbReviews,
        nbFavorites = story.nbFavorites,
        publishedDate = story.publishedDate,
        updatedDate = story.updatedDate,
        fetchedDate = story.fetchedDate,
        nbChapters = story.nbChapters,
        nbSyncedChapters = story.nbSyncedChapters
    )

    fun toChapterEntityList(
        fanfictionId: String,
        chapterList: List<Chapter>
    ): List<ChapterEntity> = chapterList.map { chapter ->
        ChapterEntity(
            fanfictionId = fanfictionId,
            chapterId = chapter.id,
            title = chapter.title,
            content = chapter.content
        )
    }

    fun toReviewEntity(fanfictionId: String, reviewList: List<Review>): List<ReviewEntity> {
        return reviewList.map { review ->
            ReviewEntity(
                fanfictionId = fanfictionId,
                chapterId = review.chapterId,
                comment = review.comment,
                poster = review.poster,
                date = review.date
            )
        }
    }

    fun toReview(reviewEntity: ReviewEntity): Review {
        return Review(
            chapterId = reviewEntity.chapterId,
            comment = reviewEntity.comment,
            poster = reviewEntity.poster,
            date = reviewEntity.date
        )
    }
}
