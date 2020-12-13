package fr.ffnet.downloader.utils

import fr.ffnet.downloader.models.Chapter
import fr.ffnet.downloader.models.Story
import fr.ffnet.downloader.models.Review
import org.joda.time.DateTime
import org.joda.time.LocalDateTime
import org.jsoup.select.Elements
import javax.inject.Inject

class FanfictionBuilder @Inject constructor(
    private val jsoupParser: JsoupParser
) {

    fun buildFanfiction(
        id: String,
        html: String
    ): Pair<String, Story> {

        val document = jsoupParser.parseHtml(html)
        val profileTop = document.select("div#profile_top")
        val dates = profileTop.select("span[data-xutime]")

        val author = profileTop.select("a").first().text()
        val title = profileTop.select("b").first()?.text() ?: "N/A"
        val words = extractWordsNb(profileTop.text())
        val summary = profileTop.select("div").last()?.text() ?: "N/A"
        val published = dates[if (dates.size > 1) 1 else 0]?.attr("data-xutime")?.toLong() ?: 0
        val updated = dates[0]?.attr("data-xutime")?.toLong() ?: 0
        val chapterList = extractChapterList(
            document.select("#chap_select"),
            title
        )

        val reviews = if (profileTop.text().contains("Reviews: ")) {
            profileTop.text().split("Reviews: ")[1].split(" ")[0].replace(",", "").toInt()
        } else 0
        val favorites = if (profileTop.text().contains("Favs: ")) {
            profileTop.text().split("Favs: ")[1].split(" ")[0].replace(",", "").toInt()
        } else 0
        val languageIndex = profileTop.text().indexOfAny(SearchBuilder.languageList)
        val genreIndex = profileTop.text().indexOfAny(SearchBuilder.genreList)
        val category = document.select("#pre_story_links>span>a").last().text()
        val image = profileTop.select("span>img.cimage").attr("src")

        return document.select("#storytext").first().html() to Story(
            id = id,
            title = title,
            isInLibrary = false,
            image = "https:$image",
            words = words,
            author = author,
            summary = summary,
            language = if (languageIndex >= 0) {
                profileTop.text().substring(languageIndex).replaceAfter(" ", "")
            } else "",
            category = category,
            genre = if (genreIndex >= 0) {
                profileTop.text().substring(genreIndex).replaceAfter(" ", "").split("/")[0]
            } else "",
            nbReviews = reviews,
            nbFavorites = favorites,
            publishedDate = DateTime(published * 1000).toDate(),
            updatedDate = DateTime(updated * 1000).toDate(),
            fetchedDate = LocalDateTime.now(),
            profileType = 0,
            nbChapters = chapterList.size,
            nbSyncedChapters = 0,
            chapterList = chapterList
        )
    }

    fun extractChapter(html: String): String {
        val document = jsoupParser.parseHtml(html)
        return document.select("#storytext").first().html()
    }

    fun buildReviews(html: String): List<Review> {
        if (html.contains("No Reviews found")) {
            return emptyList()
        }
        val document = jsoupParser.parseHtml(html)
        val reviewListSelector = document.select("table.table-striped>tbody>tr>td")

        return reviewListSelector.map { reviewSelector ->

            val date = reviewSelector
                .select("span[data-xutime]")
                .firstOrNull()
                ?.attr("data-xutime")
                ?.toLong() ?: 0

            val chapterId = reviewSelector.text().split(" chapter ")[1].split(" ")[0].trim()
            val comment = reviewSelector.select("div").first().text()
            val poster = reviewSelector.text().split(" chapter ")[0].trim()
            Review(
                chapterId = chapterId,
                comment = comment,
                poster = poster,
                date = DateTime(date * 1000).toDate()
            )
        }
    }

    private fun extractChapterList(
        select: Elements,
        title: String
    ): List<Chapter> {
        return if (select.isNotEmpty()) {
            select.first().select("option").map {
                val chapterId = it.attr("value")
                Chapter(
                    id = chapterId,
                    title = it.text().replace("$chapterId. ", "")
                )
            }
        } else {
            listOf(
                Chapter(
                    id = "1",
                    title = title
                )
            )
        }
    }

    private fun extractWordsNb(text: String): Int {
        val split = text.split("Words: ")
        if (split[1].isNotEmpty()) {
            val split2 = split[1].split(" ")
            if (split2.isNotEmpty()) {
                return split2[0].replace(",", "").trim().toInt()
            }
        }
        return 0
    }
}
