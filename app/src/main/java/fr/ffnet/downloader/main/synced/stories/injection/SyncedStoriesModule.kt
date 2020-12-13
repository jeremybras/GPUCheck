package fr.ffnet.downloader.main.synced.stories.injection

import android.content.res.Resources
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import fr.ffnet.downloader.main.synced.stories.SyncedStoriesFragment
import fr.ffnet.downloader.main.synced.stories.SyncedStoriesViewModel
import fr.ffnet.downloader.options.OptionsController
import fr.ffnet.downloader.options.OptionsViewModel
import fr.ffnet.downloader.repository.AuthorRepository
import fr.ffnet.downloader.repository.DatabaseRepository
import fr.ffnet.downloader.repository.DownloaderRepository
import fr.ffnet.downloader.repository.SettingsRepository
import fr.ffnet.downloader.utils.EpubBuilder
import fr.ffnet.downloader.utils.FanfictionOpener
import fr.ffnet.downloader.utils.PdfBuilder
import fr.ffnet.downloader.utils.UIBuilder
import fr.ffnet.downloader.utils.ViewModelFactory

@Module
class SyncedStoriesModule(private val fragment: SyncedStoriesFragment) {

    @Provides
    fun provideSyncedViewModel(
        databaseRepository: DatabaseRepository,
        downloaderRepository: DownloaderRepository,
        settingsRepository: SettingsRepository,
        uiBuilder: UIBuilder
    ): SyncedStoriesViewModel {
        val factory = ViewModelFactory {
            SyncedStoriesViewModel(
                databaseRepository,
                downloaderRepository,
                settingsRepository,
                uiBuilder
            )
        }
        return ViewModelProvider(fragment, factory)[SyncedStoriesViewModel::class.java]
    }

    @Provides
    fun provideFanfictionOpener(): FanfictionOpener = FanfictionOpener(fragment.requireContext())

    @Provides
    fun provideOptionsViewModel(
        resources: Resources,
        databaseRepository: DatabaseRepository,
        downloaderRepository: DownloaderRepository,
        authorRepository: AuthorRepository,
        pdfBuilder: PdfBuilder,
        epubBuilder: EpubBuilder
    ): OptionsViewModel {
        val factory = ViewModelFactory {
            OptionsViewModel(
                resources = resources,
                dbRepository = databaseRepository,
                apiRepository = downloaderRepository,
                authorRepository = authorRepository,
                pdfBuilder = pdfBuilder,
                epubBuilder = epubBuilder
            )
        }
        return ViewModelProvider(fragment, factory)[OptionsViewModel::class.java]
    }

    @Provides
    fun provideOptionsController(
        optionsViewModel: OptionsViewModel,
        fanfictionOpener: FanfictionOpener
    ): OptionsController {
        return OptionsController(
            context = fragment.requireContext(),
            lifecycleOwner = fragment.viewLifecycleOwner,
            parentListener = fragment,
            optionsViewModel = optionsViewModel,
            fanfictionOpener = fanfictionOpener
        )
    }
}
