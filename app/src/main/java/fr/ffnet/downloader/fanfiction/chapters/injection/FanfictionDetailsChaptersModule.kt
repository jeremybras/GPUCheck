package fr.ffnet.downloader.fanfiction.chapters.injection

import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import fr.ffnet.downloader.fanfiction.chapters.FanfictionDetailsChaptersFragment
import fr.ffnet.downloader.fanfiction.chapters.FanfictionDetailsChaptersViewModel
import fr.ffnet.downloader.repository.DatabaseRepository
import fr.ffnet.downloader.utils.ViewModelFactory

@Module
class FanfictionDetailsChaptersModule(private val fragment: FanfictionDetailsChaptersFragment) {

    @Provides
    fun provideViewModel(
        databaseRepository: DatabaseRepository,
    ): FanfictionDetailsChaptersViewModel {
        val factory = ViewModelFactory {
            FanfictionDetailsChaptersViewModel(
                databaseRepository = databaseRepository
            )
        }
        return ViewModelProvider(fragment, factory)[FanfictionDetailsChaptersViewModel::class.java]
    }
}
