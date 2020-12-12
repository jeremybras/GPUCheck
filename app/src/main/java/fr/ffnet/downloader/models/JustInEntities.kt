package fr.ffnet.downloader.models

sealed class JustInUI {

    object JustInError : JustInUI()
    data class JustInSuccess(val justInList: List<JustInUIItem>) : JustInUI()

    data class JustInUIItem(
        val imageUrl: String,
        val storyId: String
    )
}
