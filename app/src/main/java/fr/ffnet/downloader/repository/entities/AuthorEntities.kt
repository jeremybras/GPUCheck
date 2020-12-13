package fr.ffnet.downloader.repository.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.joda.time.LocalDateTime

data class AuthorSearchResult(
    val id: String,
    val name: String,
    val nbStories: String,
    val imageUrl: String
)

data class Author(
    val id: String,
    val name: String,
    val nbStories: Int,
    val nbFavorites: Int,
    val fetchedDate: LocalDateTime
)

@Entity
data class AuthorEntity(
    @PrimaryKey val authorId: String,
    val name: String,
    val fetchedDate: LocalDateTime,
    val nbstories: Int,
    val nbFavorites: Int
)
