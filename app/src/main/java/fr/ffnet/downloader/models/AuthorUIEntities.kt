package fr.ffnet.downloader.models

data class AuthorUI(
    val title: String,
    val nbStories: String,
    val nbFavorites: String
)

sealed class AuthorUIItem {

    data class SearchAuthorNotResultUIItem(
        val message: String
    ) : AuthorUIItem()

    data class AuthorTitleUIItem(
        val title: String
    ) : AuthorUIItem()

    data class SyncedAuthorUIItem(
        val id: String
    ) : AuthorUIItem()

    data class SearchAuthorUIItem(
        val id: String
    ) : AuthorUIItem()
}