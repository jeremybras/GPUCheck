package fr.ffnet.downloader.fanfiction.summary

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import fr.ffnet.downloader.models.SearchUIItem.*
import fr.ffnet.downloader.repository.DatabaseRepository
import fr.ffnet.downloader.utils.UIBuilder

class FanfictionDetailsSummaryViewModel(
    private val databaseRepository: DatabaseRepository,
    private val uiBuilder: UIBuilder
) : ViewModel() {

    fun loadStoryUI(fanfictionId: String): LiveData<SearchStoryUI> {
        return Transformations.map(databaseRepository.getFanfictionInfo(fanfictionId)) {
            uiBuilder.buildSearchStoryUI(it)
        }
    }
}
