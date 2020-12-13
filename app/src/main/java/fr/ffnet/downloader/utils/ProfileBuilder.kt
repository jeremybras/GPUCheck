package fr.ffnet.downloader.utils

import fr.ffnet.downloader.models.Story
import fr.ffnet.downloader.models.Profile
import fr.ffnet.downloader.repository.AuthorRepository.Companion.PROFILE_TYPE_FAVORITE
import fr.ffnet.downloader.repository.AuthorRepository.Companion.PROFILE_TYPE_MY_STORY
import org.joda.time.DateTime
import org.jsoup.select.Elements
import javax.inject.Inject

class ProfileBuilder @Inject constructor(
    private val jsoupParser: JsoupParser
) {
    fun buildProfile(profileId: String, html: String): Profile {
        val document = jsoupParser.parseHtml(html)

        val name = document.select("#content_wrapper_inner span").first()?.text() ?: "N/A"
        val favoriteStoriesSelector = document.select(".z-list.favstories")
        val myStoriesSelector = document.select(".z-list.mystories")

        return Profile(
            profileId = profileId,
            name = name,
            favoriteStoryList = extractFanfictionsFromList(
                favoriteStoriesSelector,
                PROFILE_TYPE_FAVORITE
            ),
            myStoryList = extractFanfictionsFromList(
                myStoriesSelector,
                PROFILE_TYPE_MY_STORY,
                name
            )
        )
    }

    private fun extractFanfictionsFromList(
        selector: Elements,
        profileType: Int,
        defaultAuthor: String? = null
    ): List<Story> {
        return selector.map { fanfiction ->
            val fanfictionId = fanfiction.attr("data-storyId")
            val image = fanfiction.select("a img").attr("data-original")

            val fanfictionStuff = fanfiction.select(".z-padtop2").first().text()
            val reviews = if (fanfictionStuff.contains("Reviews: ")) {
                fanfictionStuff.split("Reviews: ")[1].split(" ")[0].replace(",", "").toInt()
            } else 0
            val favorites = if (fanfictionStuff.contains("Favs: ")) {
                fanfictionStuff.split("Favs: ")[1].split(" ")[0].replace(",", "").toInt()
            } else 0
            val languageIndex = fanfictionStuff.indexOfAny(SearchBuilder.languageList)
            val genreIndex = fanfictionStuff.indexOfAny(SearchBuilder.genreList)
            val category = fanfictionStuff.split(" -")[0]


            Story(
                id = fanfictionId,
                title = fanfiction.attr("data-title"),
                isInLibrary = false,
                image = "https:$image",
                words = fanfiction.attr("data-wordcount").toInt(),
                author = defaultAuthor ?: fanfiction
                    .select("a")
                    .first { it.attr("href").contains("/u/") }
                    .text(),
                summary = "N/A",
                language = if (languageIndex >= 0) {
                    fanfictionStuff.substring(languageIndex).replaceAfter(" ", "")
                } else "",
                category = category,
                genre = if (genreIndex >= 0) {
                    fanfictionStuff.substring(genreIndex).replaceAfter(" ", "").split("/")[0]
                } else "",
                nbReviews = reviews,
                nbFavorites = favorites,
                publishedDate = DateTime(
                    fanfiction.attr("data-datesubmit")
                        .toLong() * 1000
                ).toDate(),
                updatedDate = DateTime(fanfiction.attr("data-dateupdate").toLong() * 1000).toDate(),
                fetchedDate = null,
                profileType = profileType,
                nbChapters = fanfiction.attr("data-chapters").toInt(),
                nbSyncedChapters = 0,
                chapterList = emptyList()
            )
        }
    }
}
