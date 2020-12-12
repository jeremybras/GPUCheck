package fr.ffnet.downloader.models

import fr.ffnet.downloader.fanfiction.FanfictionViewModel.StoryState

sealed class SyncedUIItem {

    data class SyncedUITitle(
        val title: String,
        val subtitle: String,
    ) : SyncedUIItem()

    data class SyncedStoryUI(
        val id: String,
        val title: String,
        val details: String,
        val imageUrl: String,
        val storyState: StoryState,
        val shouldShowExportPdf: Boolean,
        val shouldShowExportEpub: Boolean
    ) : SyncedUIItem()

    data class SyncedStorySpotlightUI(
        val id: String,
        val title: String,
        val details: String,
        val imageUrl: String,
        val storyState: StoryState,
        val shouldShowExportPdf: Boolean,
        val shouldShowExportEpub: Boolean
    ) : SyncedUIItem()
}
