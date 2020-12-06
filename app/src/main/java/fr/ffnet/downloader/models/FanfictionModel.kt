package fr.ffnet.downloader.models

import org.joda.time.LocalDateTime
import java.util.*

data class Fanfiction(
    val id: String,
    var title: String,
    val isInLibrary: Boolean,
    val image: String,
    var words: Int,
    val author: String,
    var summary: String,
    val language: String,
    val category: String,
    val genre: String,
    val nbReviews: Int,
    val nbFavorites: Int,
    val publishedDate: Date,
    val updatedDate: Date,
    val fetchedDate: LocalDateTime?,
    val profileType: Int,
    val nbChapters: Int,
    val nbSyncedChapters: Int,
    val chapterList: List<Chapter> = emptyList()
)

data class Chapter(
    val id: String,
    val title: String,
    var content: String = ""
)

data class Review(
    val chapterId: String,
    val comment: String,
    val poster: String,
    val date: Date
)
