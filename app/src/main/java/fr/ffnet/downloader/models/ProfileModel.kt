package fr.ffnet.downloader.models

data class Profile(
    val profileId: String,
    val name: String,
    val favoriteStoryList: List<Story>,
    val myStoryList: List<Story>,
    val imageUrl: String
)
