package fr.ffnet.downloader.models

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