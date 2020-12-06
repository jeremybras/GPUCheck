package fr.ffnet.downloader.fanfiction.reviews.injection

import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import fr.ffnet.downloader.fanfiction.reviews.FanfictionDetailsReviewsFragment
import fr.ffnet.downloader.fanfiction.reviews.FanfictionDetailsReviewsViewModel
import fr.ffnet.downloader.repository.DatabaseRepository
import fr.ffnet.downloader.repository.DownloaderRepository
import fr.ffnet.downloader.utils.UIBuilder
import fr.ffnet.downloader.utils.ViewModelFactory

@Module
class FanfictionDetailsReviewsModule(private val fragment: FanfictionDetailsReviewsFragment) {

    @Provides
    fun provideViewModel(
        databaseRepository: DatabaseRepository,
        downloaderRepository: DownloaderRepository,
        uiBuilder: UIBuilder
    ): FanfictionDetailsReviewsViewModel {
        val factory = ViewModelFactory {
            FanfictionDetailsReviewsViewModel(
                databaseRepository = databaseRepository,
                downloaderRepository = downloaderRepository,
                uiBuilder = uiBuilder
            )
        }
        return ViewModelProvider(fragment, factory)[FanfictionDetailsReviewsViewModel::class.java]
    }


}
