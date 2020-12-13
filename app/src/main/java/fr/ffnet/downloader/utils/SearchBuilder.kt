package fr.ffnet.downloader.utils

import fr.ffnet.downloader.models.Story
import fr.ffnet.downloader.repository.entities.AuthorSearchResult
import org.joda.time.DateTime
import javax.inject.Inject

class SearchBuilder @Inject constructor(
    private val jsoupParser: JsoupParser
) {

    companion object {

        val languageList = listOf(
            "Afrikaans",
            "Bahasa Indonesia",
            "Bahasa Melayu",
            "Català",
            "Dansk",
            "Deutsch",
            "Eesti",
            "English",
            "Español",
            "Esperanto",
            "Filipino",
            "Français",
            "Hrvatski jezik",
            "Italiano",
            "Język polski",
            "LINGUA LATINA",
            "Magyar",
            "Nederlands",
            "Norsk",
            "Português",
            "Română",
            "Shqip",
            "Slovenčina",
            "Suomi",
            "Svenska",
            "Tiếng Việt",
            "Türkçe",
            "Íslenska",
            "čeština",
            "Ελληνικά",
            "България",
            "Русский",
            "Українська",
            "српски",
            "עברית",
            "العربية",
            "فارسی",
            "देवनागरी",
            "हिंदी",
            "ภาษาไทย",
            "中文",
            "日本語",
            "한국어"
        )
        val genreList = listOf(
            "Romance",
            "General",
            "Humor",
            "Drama",
            "Adventure",
            "Hurt/Comfort",
            "Angst",
            "Friendship",
            "Family",
            "Tragedy",
            "Supernatural",
            "Fantasy",
            "Horror",
            "Suspense",
            "Mystery",
            "Sci-Fi",
            "Parody",
            "Crime",
            "Poetry",
            "Spiritual",
            "Western"
        )
    }

    fun buildFanfictionSearchResult(html: String): List<Story> {
        val document = jsoupParser.parseHtml(html)
        val selector = document.select(".z-list")
        return selector.map { fanfiction ->

            val fanfictionStuff = fanfiction.select(".z-padtop2").first().text()

            val words = fanfictionStuff.split("Words: ")[1].split(" ")[0].replace(",", "").toInt()
            val chapters = fanfictionStuff.split("Chapters: ")[1].split(" ")[0]
            val dates = fanfiction.select("span[data-xutime]")
            val updatedDate = DateTime(dates[0].attr("data-xutime").toLong() * 1000).toDate()
            val publishedDate = DateTime(
                dates[dates.size - 1].attr("data-xutime").toLong() * 1000
            ).toDate()

            val reviews = if (fanfictionStuff.contains("Reviews: ")) {
                fanfictionStuff.split("Reviews: ")[1].split(" ")[0].replace(",", "").toInt()
            } else 0
            val favorites = if (fanfictionStuff.contains("Favs: ")) {
                fanfictionStuff.split("Favs: ")[1].split(" ")[0].replace(",", "").toInt()
            } else 0
            val languageIndex = fanfictionStuff.indexOfAny(languageList)
            val genreIndex = fanfictionStuff.indexOfAny(genreList)
            val category = fanfictionStuff.split(" -")[0]
            val image = fanfiction.select("a.stitle>img").attr("data-original")

            val fanfictionId = fanfiction.select("a.stitle").first().attr("href").split("/")[2]
            Story(
                id = fanfictionId,
                title = fanfiction.select("a.stitle").text(),
                isInLibrary = false,
                image = "https:$image",
                words = words,
                author = fanfiction.select("a").first { it.attr("href").contains("/u/") }.text(),
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
                publishedDate = publishedDate,
                updatedDate = updatedDate,
                fetchedDate = null,
                profileType = 0,
                nbChapters = chapters.toInt(),
                nbSyncedChapters = 0,
                chapterList = emptyList()
            )
        }
    }

    fun buildAuthorSearchResult(html: String): List<AuthorSearchResult> {
        val document = jsoupParser.parseHtml(html)

        val selector = document.select("div>table>tbody td>a")

        return if (selector.size == 1) {
            if (html.contains("1 found")) {
                val container = document.select("div#content_wrapper_inner").first()
                val link = container.select("a").first { it.attr("href").contains("/u/") }
                val image = container.select("img").attr("data-original")

                val nbStoriesContainer = container.select("span.badge-blue")
                val nbStories = if (nbStoriesContainer.size == 1) nbStoriesContainer.text() else "0"
                listOf(
                    AuthorSearchResult(
                        id = link.attr("href").split("/")[2],
                        name = link.text(),
                        nbStories = nbStories,
                        imageUrl = "https:$image"
                    )
                )
            } else emptyList()
        } else {
            selector.mapNotNull {
                if (it.attr("href").contains("/u/")) {
                    val span = it.select("span")
                    val image = it.select("img").attr("data-original")
                    AuthorSearchResult(
                        id = it.attr("href").split("/")[2],
                        name = it.select("b").first().text(),
                        nbStories = span.select("span").text().split(" ")[0],
                        imageUrl = "https:$image"
                    )
                } else null
            }
        }
    }
}
