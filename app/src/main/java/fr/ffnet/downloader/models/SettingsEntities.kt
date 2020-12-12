package fr.ffnet.downloader.models

data class Setting(
    val type: SettingType,
    val isEnabled: Boolean
)

enum class SettingType {
    PDF_EXPORT,
    EPUB_EXPORT,
    MOBI_EXPORT,

    DEFAULT_SEARCH_ALL,
    DEFAULT_SEARCH_AUTHORS,
    DEFAULT_SEARCH_STORIES,

    JUST_IN_SHOW_SECTION,
    JUST_IN_RECENTLY_PUBLISHED,
    JUST_IN_RECENTLY_UPDATED
}
