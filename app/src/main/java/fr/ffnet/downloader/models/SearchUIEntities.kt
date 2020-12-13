package fr.ffnet.downloader.models

sealed class SearchUIItem {
    data class SearchUITitle(
        val title: String
    ) : SearchUIItem()

    data class SearchStoryUI(
        val id: String,
        val title: String,
        val summary: String,
        val details: String,
        val publishedDate: String,
        val updatedDate: String,
        val language: String,
        val genre: String,
        val chaptersNb: Int,
        val syncedChaptersNb: Int,
        val chaptersMissingText: String,
        val isDownloadComplete: Boolean,
        val reviewsNb: String,
        val favoritesNb: String,
        val imageUrl: String
    ) : SearchUIItem()

    data class SearchAuthorUI(
        val id: String,
        val name: String,
        val nbStories: String,
        val imageUrl: String,
        val actionImage: Int
    ) : SearchUIItem()
}

data class ChapterUI(
    val id: String,
    val title: String,
    val status: ChapterSyncState
)

data class ReviewUI(
    val poster: String,
    val chapter: String,
    val date: String,
    val comment: String
)

enum class ChapterSyncState {
    SYNCED, UNSYNCED
}
