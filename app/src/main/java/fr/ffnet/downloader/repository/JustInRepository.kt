package fr.ffnet.downloader.repository

import fr.ffnet.downloader.main.search.JustInViewModel.JustInType
import fr.ffnet.downloader.models.Story
import fr.ffnet.downloader.utils.SearchBuilder
import java.io.IOException

class JustInRepository(
    private val regularCrawlService: RegularCrawlService,
    private val searchBuilder: SearchBuilder
) {

    fun loadJustInList(type: JustInType): List<Story>? {
        try {
            val response = when (type) {
                JustInType.PUBLISHED -> regularCrawlService.justInPublished().execute()
                JustInType.UPDATED -> regularCrawlService.justInUpdated().execute()
            }
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
}
