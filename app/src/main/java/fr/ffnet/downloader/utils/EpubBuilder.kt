package fr.ffnet.downloader.utils

import fr.ffnet.downloader.models.Chapter
import fr.ffnet.downloader.models.Fanfiction
import nl.siegmann.epublib.domain.Book
import nl.siegmann.epublib.domain.Resource
import nl.siegmann.epublib.epub.EpubWriter
import nl.siegmann.epublib.service.MediatypeService
import org.apache.commons.text.StringEscapeUtils
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import javax.inject.Inject

class EpubBuilder @Inject constructor() {

    companion object {
        private const val HTML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<!DOCTYPE html>\n<html xmlns=\"http://www.w3.org/1999/xhtml\" >\n\n<head>\n" +
            "<meta charset=\"utf-8\" />\n"
        private const val HTML_FOOTER = "</body>\n</html>"
    }

    fun buildEpub(absolutePath: String, fanfiction: Fanfiction): String {

        val fileTitle = "${fanfiction.title}.epub"
        val file = File(absolutePath, fileTitle)

        val outputStream = ByteArrayOutputStream()
        val epubWriter = EpubWriter()
        val book = Book()

        book.metadata.apply {
            addTitle(fanfiction.title)
        }

        fanfiction.chapterList.forEach { chapter ->
            val chapterHtml = generateHtmlPageFromChapter(chapter)
            book.addSection(
                chapter.title,
                Resource(chapterHtml.toByteArray(), MediatypeService.XHTML)
            )
        }

        epubWriter.write(book, outputStream)

        val inputStream = ByteArrayInputStream(outputStream.toByteArray())
        inputStream.use { input ->
            file.outputStream().use { input.copyTo(it) }
        }
        return fileTitle
    }

    private fun generateHtmlPageFromChapter(chapter: Chapter): String {
        var htmlPage = chapter.content
        htmlPage = "$HTML_HEADER<title>" + StringEscapeUtils.escapeHtml4(
            chapter.title
        ) + "</title>\n</head>\n<body>\n" + htmlPage + HTML_FOOTER
        htmlPage = htmlPage
            .replace("noshade", "")
            .replace("<br>", "<br/>")
            .replace("<hr size=\"1\" >", "<hr/>")
        return htmlPage
    }
}
