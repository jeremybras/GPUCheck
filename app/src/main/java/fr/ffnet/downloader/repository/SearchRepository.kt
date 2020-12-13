package fr.ffnet.downloader.repository

import fr.ffnet.downloader.models.Story
import fr.ffnet.downloader.repository.entities.AuthorSearchResult
import fr.ffnet.downloader.utils.SearchBuilder
import java.io.IOException

class SearchRepository(
    private val regularCrawlService: RegularCrawlService,
    private val mobileCrawlService: MobileCrawlService,
    private val searchBuilder: SearchBuilder
) {

    companion object {
        private const val SEARCH_TYPE_STORY = "story"
        private const val SEARCH_TYPE_WRITER = "writer"
    }

    fun searchStory(keywordList: List<String>): List<Story>? {
        val keywords = keywordList.joinToString("+")

        try {
            val response = regularCrawlService.search(keywords, SEARCH_TYPE_STORY).execute()
            if (response.isSuccessful) {
                response.body()?.let { responseBody ->
                    return searchBuilder.buildFanfictionSearchResult(responseBody.string())
                }
            }
        } catch (ex: IOException) {
            return null
        }
        return emptyList()
    }

    fun searchAuthor(keywordList: List<String>): List<AuthorSearchResult>? {
        val keywords = keywordList.joinToString("+")

        try {
            val response = regularCrawlService.search(keywords, SEARCH_TYPE_WRITER).execute()
            if (response.isSuccessful) {
                response.body()?.let { responseBody ->
                    return searchBuilder.buildAuthorSearchResult(responseBody.string())
                }
            }
        } catch (ex: IOException) {
            return null
        }
        return emptyList()
    }
}
