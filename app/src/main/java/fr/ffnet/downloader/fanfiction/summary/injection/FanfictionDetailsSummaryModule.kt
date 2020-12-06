package fr.ffnet.downloader.fanfiction.summary.injection

import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import fr.ffnet.downloader.fanfiction.summary.FanfictionDetailsSummaryFragment
import fr.ffnet.downloader.fanfiction.summary.FanfictionDetailsSummaryViewModel
import fr.ffnet.downloader.repository.DatabaseRepository
import fr.ffnet.downloader.utils.UIBuilder
import fr.ffnet.downloader.utils.ViewModelFactory

@Module
class FanfictionDetailsSummaryModule(private val fragment: FanfictionDetailsSummaryFragment) {

    @Provides
    fun provideViewModel(
        databaseRepository: DatabaseRepository,
        uiBuilder: UIBuilder
    ): FanfictionDetailsSummaryViewModel {
        val factory = ViewModelFactory {
            FanfictionDetailsSummaryViewModel(
                databaseRepository = databaseRepository,
                uiBuilder = uiBuilder
            )
        }
        return ViewModelProvider(fragment, factory)[FanfictionDetailsSummaryViewModel::class.java]
    }
}
