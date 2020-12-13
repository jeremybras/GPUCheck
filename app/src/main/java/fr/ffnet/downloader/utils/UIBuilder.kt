package fr.ffnet.downloader.utils

import android.content.res.Resources
import fr.ffnet.downloader.R
import fr.ffnet.downloader.fanfiction.FanfictionViewModel.StoryState
import fr.ffnet.downloader.models.Review
import fr.ffnet.downloader.models.ReviewUI
import fr.ffnet.downloader.models.SearchUIItem.SearchAuthorUI
import fr.ffnet.downloader.models.SearchUIItem.SearchStoryUI
import fr.ffnet.downloader.models.Story
import fr.ffnet.downloader.models.SyncedUIItem.SyncedAuthorUI
import fr.ffnet.downloader.models.SyncedUIItem.SyncedStorySpotlightUI
import fr.ffnet.downloader.models.SyncedUIItem.SyncedStoryUI
import fr.ffnet.downloader.repository.entities.AuthorSearchResult
import javax.inject.Inject

class UIBuilder @Inject constructor(
    private val resources: Resources,
    private val dateFormatter: DateFormatter,
) {

    fun buildSearchAuthorUI(
        author: AuthorSearchResult,
        actionImage: Int
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
            imageUrl = author.imageUrl,
            actionImage = actionImage
        )
    }

    fun buildAuthorUI(name: String, nbStories: Int, nbFavorites: Int): SyncedAuthorUI {
        return SyncedAuthorUI(
            title = resources.getString(R.string.author_title, name),
            nbStories = resources.getQuantityString(
                R.plurals.search_author_nb_stories,
                nbStories,
                nbStories
            ),
            nbFavorites = resources.getQuantityString(
                R.plurals.search_author_nb_favorites,
                nbFavorites,
                nbFavorites
            )
        )
    }

    fun buildSearchStoryUI(
        story: Story,
    ): SearchStoryUI {
        return SearchStoryUI(
            id = story.id,
            title = story.title,
            summary = story.summary,
            details = "${story.category} / ${story.author}",
            publishedDate = dateFormatter.format(story.publishedDate),
            updatedDate = dateFormatter.format(story.updatedDate),
            language = story.language,
            genre = story.genre,
            chaptersNb = story.nbChapters,
            syncedChaptersNb = story.nbSyncedChapters,
            chaptersMissingText = resources.getQuantityString(
                R.plurals.story_chapters_missing,
                story.nbChapters - story.nbSyncedChapters,
                story.nbChapters - story.nbSyncedChapters
            ),
            isDownloadComplete = story.nbSyncedChapters == story.nbChapters,
            reviewsNb = story.nbReviews.toString(),
            favoritesNb = story.nbFavorites.toString(),
            imageUrl = story.image
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
        story: Story,
        storyState: StoryState,
        shouldShowExportPdf: Boolean,
        shouldShowExportEpub: Boolean
    ): SyncedStoryUI {
        return SyncedStoryUI(
            id = story.id,
            title = story.title,
            details = "${story.category} / ${story.author}",
            imageUrl = story.image,
            storyState = storyState,
            shouldShowExportPdf = shouldShowExportPdf,
            shouldShowExportEpub = shouldShowExportEpub
        )
    }

    fun buildSyncedStorySpotlightUI(
        story: Story,
        storyState: StoryState,
        shouldShowExportPdf: Boolean,
        shouldShowExportEpub: Boolean
    ): SyncedStorySpotlightUI {
        return SyncedStorySpotlightUI(
            id = story.id,
            title = story.title,
            details = "${story.category} / ${story.author}",
            imageUrl = story.image,
            storyState = storyState,
            shouldShowExportPdf = shouldShowExportPdf,
            shouldShowExportEpub = shouldShowExportEpub
        )
    }
}
