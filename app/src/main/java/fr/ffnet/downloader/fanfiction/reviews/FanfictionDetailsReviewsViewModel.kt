package fr.ffnet.downloader.fanfiction.reviews

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.ffnet.downloader.models.ReviewUI
import fr.ffnet.downloader.repository.DatabaseRepository
import fr.ffnet.downloader.repository.DownloaderRepository
import fr.ffnet.downloader.utils.UIBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FanfictionDetailsReviewsViewModel(
    private val databaseRepository: DatabaseRepository,
    private val downloaderRepository: DownloaderRepository,
    private val uiBuilder: UIBuilder
) : ViewModel() {

    fun loadReviews(fanfictionId: String): LiveData<List<ReviewUI>> {
        viewModelScope.launch(Dispatchers.IO) {
            downloaderRepository.loadReviews(fanfictionId)
        }
        return Transformations.map(databaseRepository.loadReviews(fanfictionId)) { reviewList ->
            reviewList.map { review ->
                uiBuilder.buildReviewUI(review)
            }
        }
    }
}
