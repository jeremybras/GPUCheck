package fr.ffnet.downloader.utils

import android.content.res.Resources
import fr.ffnet.downloader.R
import fr.ffnet.downloader.fanfiction.FanfictionViewModel.StoryState
import fr.ffnet.downloader.models.AuthorUI
import fr.ffnet.downloader.models.Fanfiction
import fr.ffnet.downloader.models.Review
import fr.ffnet.downloader.models.ReviewUI
import fr.ffnet.downloader.models.SearchUIItem.SearchAuthorUI
import fr.ffnet.downloader.models.SearchUIItem.SearchStoryUI
import fr.ffnet.downloader.models.SyncedUIItem.SyncedStorySpotlightUI
import fr.ffnet.downloader.models.SyncedUIItem.SyncedStoryUI
import fr.ffnet.downloader.repository.AuthorRepository.AuthorRepositoryResult.AuthorRepositoryResultSuccess
import fr.ffnet.downloader.repository.entities.AuthorSearchResult
import javax.inject.Inject

class UIBuilder @Inject constructor(
    private val resources: Resources,
    private val dateFormatter: DateFormatter,
) {

    fun buildSearchAuthorUI(
        author: AuthorSearchResult,
    ): SearchAuthorUI {
        val nbStories = author.nbStories.toInt()
        return SearchAuthorUI(
            id = author.id,
            name = author.name,
            nbStories = resources.getQuantityString(
                R.plurals.search_author_nb_stories,
                nbStories,
                nbStories
            ),
            imageUrl = author.imageUrl
        )
    }

    fun buildSearchStoryUI(
        fanfiction: Fanfiction,
    ): SearchStoryUI {
        return SearchStoryUI(
            id = fanfiction.id,
            title = fanfiction.title,
            summary = fanfiction.summary,
            details = "${fanfiction.category} / ${fanfiction.author}",
            publishedDate = dateFormatter.format(fanfiction.publishedDate),
            updatedDate = dateFormatter.format(fanfiction.updatedDate),
            language = fanfiction.language,
            genre = fanfiction.genre,
            chaptersNb = fanfiction.nbChapters,
            syncedChaptersNb = fanfiction.nbSyncedChapters,
            chaptersMissingText = resources.getQuantityString(
                R.plurals.story_chapters_missing,
                fanfiction.nbChapters - fanfiction.nbSyncedChapters,
                fanfiction.nbChapters - fanfiction.nbSyncedChapters
            ),
            isDownloadComplete = fanfiction.nbSyncedChapters == fanfiction.nbChapters,
            reviewsNb = fanfiction.nbReviews.toString(),
            favoritesNb = fanfiction.nbFavorites.toString(),
            imageUrl = fanfiction.image
        )
    }

    fun buildReviewUI(review: Review): ReviewUI {
        return ReviewUI(
            chapter = resources.getString(
                R.string.story_review_chapter_title,
                review.chapterId
            ),
            comment = review.comment,
            poster = review.poster,
            date = dateFormatter.format(review.date)
        )
    }

    fun buildSyncedStoryUI(
        fanfiction: Fanfiction,
        storyState: StoryState,
        shouldShowExportPdf: Boolean,
        shouldShowExportEpub: Boolean
    ): SyncedStoryUI {
        return SyncedStoryUI(
            id = fanfiction.id,
            title = fanfiction.title,
            details = "${fanfiction.category} / ${fanfiction.author}",
            imageUrl = fanfiction.image,
            storyState = storyState,
            shouldShowExportPdf = shouldShowExportPdf,
            shouldShowExportEpub = shouldShowExportEpub
        )
    }

    fun buildSyncedStorySpotlightUI(
        fanfiction: Fanfiction,
        storyState: StoryState,
        shouldShowExportPdf: Boolean,
        shouldShowExportEpub: Boolean
    ): SyncedStorySpotlightUI {
        return SyncedStorySpotlightUI(
            id = fanfiction.id,
            title = fanfiction.title,
            details = "${fanfiction.category} / ${fanfiction.author}",
            imageUrl = fanfiction.image,
            storyState = storyState,
            shouldShowExportPdf = shouldShowExportPdf,
            shouldShowExportEpub = shouldShowExportEpub
        )
    }

    fun buildAuthorUI(author: AuthorRepositoryResultSuccess): AuthorUI {
        return AuthorUI(
            title = resources.getString(R.string.author_title, author.authorName),
            nbStories = resources.getQuantityString(
                R.plurals.search_author_nb_stories,
                author.storiesNb,
                author.storiesNb
            ),
            nbFavorites = resources.getQuantityString(
                R.plurals.search_author_nb_favorites,
                author.favoritesNb,
                author.favoritesNb
            )
        )
    }
}
