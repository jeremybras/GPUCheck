package fr.ffnet.downloader.utils

import fr.ffnet.downloader.models.Fanfiction
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

    fun buildFanfictionSearchResult(html: String): List<Fanfiction> {
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
            Fanfiction(
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

    fun builAuthorSearchResult(html: String): List<AuthorSearchResult> {
        val document = jsoupParser.parseHtml(html)
        val selector = document.select("form>div>div.bs")

        if (selector.isEmpty()) {
            return document.select("form a")
                .filter { it.attr("href").contains("/u/") }
                .map { link ->
                    AuthorSearchResult(
                        id = link.attr("href").split("/")[2],
                        name = link.text(),
                        nbStories = "0"
                    )
                }
        }

        return selector.map {
            val link = it.select("a")
            val span = it.select("span")
            AuthorSearchResult(
                id = link.attr("href").split("/")[2],
                name = link.text(),
                nbStories = span.select("span").text()
            )
        }
    }
}
