package fr.ffnet.downloader.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import fr.ffnet.downloader.models.Story
import fr.ffnet.downloader.repository.dao.AuthorDao
import fr.ffnet.downloader.repository.dao.FanfictionDao
import fr.ffnet.downloader.repository.entities.Author
import fr.ffnet.downloader.repository.entities.AuthorEntity
import fr.ffnet.downloader.repository.entities.ProfileFanfictionEntity
import fr.ffnet.downloader.utils.FanfictionConverter
import fr.ffnet.downloader.utils.ProfileBuilder
import org.joda.time.LocalDateTime

class AuthorRepository(
    private val regularCrawlService: RegularCrawlService,
    private val dao: AuthorDao,
    private val fanfictionDao: FanfictionDao,
    private val profileBuilder: ProfileBuilder,
    private val fanfictionConverter: FanfictionConverter
) {

    companion object {
        const val PROFILE_TYPE_FAVORITE = 1
        const val PROFILE_TYPE_MY_STORY = 2
    }

    fun unsyncAuthor(authorId: String) {
        dao.deleteProfileMapping(authorId)
        dao.deleteAuthor(authorId)
    }

    fun loadProfileInfo(authorId: String): Author? {
        val response = regularCrawlService.getProfile(authorId).execute()
        if (response.isSuccessful) {
            response.body()?.let { responseBody ->

                val profileInfo = profileBuilder.buildProfile(authorId, responseBody.string())

                val favoriteIds = insertListAndReturnIds(profileInfo.favoriteStoryList)
                val storyIds = insertListAndReturnIds(profileInfo.myStoryList)

                dao.deleteProfileMapping(authorId)
                favoriteIds.map {
                    dao.insertProfileFanfiction(
                        ProfileFanfictionEntity(
                            profileId = authorId,
                            fanfictionId = it,
                            profileType = PROFILE_TYPE_FAVORITE
                        )
                    )
                }
                storyIds.map {
                    dao.insertProfileFanfiction(
                        ProfileFanfictionEntity(
                            profileId = authorId,
                            fanfictionId = it,
                            profileType = PROFILE_TYPE_MY_STORY
                        )
                    )
                }

                val authorEntity = dao.getAuthor(authorId)
                if (authorEntity == null) {
                    val newAuthorEntity = AuthorEntity(
                        authorId = profileInfo.profileId,
                        name = profileInfo.name,
                        fetchedDate = LocalDateTime.now(),
                        nbstories = storyIds.size,
                        nbFavorites = favoriteIds.size,
                        imageUrl = profileInfo.imageUrl
                    )
                    dao.insertAuthor(newAuthorEntity)
                    return buildAuthor(newAuthorEntity)
                } else {
                    val updatedAuthorEntity = authorEntity.copy(
                        fetchedDate = LocalDateTime.now(),
                        nbstories = storyIds.size,
                        nbFavorites = favoriteIds.size,
                        imageUrl = profileInfo.imageUrl
                    )
                    dao.updateAuthor(updatedAuthorEntity)
                    return buildAuthor(updatedAuthorEntity)
                }
            }
        }
        return null
    }

    fun loadSyncedAuthors(): LiveData<List<Author>> {
        return Transformations.map(dao.getSyncedAuthors()) { authorEntityList ->
            authorEntityList.map {
                buildAuthor(it)
            }
        }
    }

    fun getAuthor(authorId: String): Author? {
        return dao.getAuthor(authorId)?.let {
            buildAuthor(it)
        }
    }

    private fun buildAuthor(authorEntity: AuthorEntity): Author {
        return Author(
            id = authorEntity.authorId,
            name = authorEntity.name,
            nbStories = authorEntity.nbstories,
            nbFavorites = authorEntity.nbFavorites,
            fetchedDate = authorEntity.fetchedDate,
            imageUrl = authorEntity.imageUrl
        )
    }

    private fun insertListAndReturnIds(storyList: List<Story>): List<String> {
        return storyList.map { fanfiction ->
            val fanfictionInfo = fanfictionDao.getFanfiction(fanfiction.id)
            if (fanfictionInfo == null) {
                val fanfictionEntity = fanfictionConverter.toFanfictionEntity(fanfiction)
                fanfictionDao.insertFanfiction(fanfictionEntity)
            }
            fanfiction.id
        }
    }
}
