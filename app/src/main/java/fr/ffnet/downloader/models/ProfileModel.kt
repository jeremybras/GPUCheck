package fr.ffnet.downloader.models

data class Profile(
    val profileId: String,
    val name: String,
    val favoriteFanfictionList: List<Fanfiction>,
    val myFanfictionList: List<Fanfiction>
)
