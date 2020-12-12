package fr.ffnet.downloader.main.search.injection

import android.content.res.Resources
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import fr.ffnet.downloader.main.ViewPagerViewModel
import fr.ffnet.downloader.main.search.JustInViewModel
import fr.ffnet.downloader.main.search.SearchFragment
import fr.ffnet.downloader.main.search.SearchViewModel
import fr.ffnet.downloader.options.OptionsController
import fr.ffnet.downloader.options.OptionsViewModel
import fr.ffnet.downloader.repository.DatabaseRepository
import fr.ffnet.downloader.repository.DownloaderRepository
import fr.ffnet.downloader.repository.JustInRepository
import fr.ffnet.downloader.repository.SearchRepository
import fr.ffnet.downloader.repository.SettingsRepository
import fr.ffnet.downloader.settings.SettingsViewModel
import fr.ffnet.downloader.utils.EpubBuilder
import fr.ffnet.downloader.utils.FanfictionOpener
import fr.ffnet.downloader.utils.PdfBuilder
import fr.ffnet.downloader.utils.UIBuilder
import fr.ffnet.downloader.utils.UrlTransformer
import fr.ffnet.downloader.utils.ViewModelFactory

@Module
class SearchModule(private val fragment: SearchFragment) {

    @Provides
    fun provideSettingsViewModel(
        repository: SettingsRepository
    ): SettingsViewModel {
        val factory = ViewModelFactory {
            SettingsViewModel(repository)
        }
        return ViewModelProvider(fragment, factory)[SettingsViewModel::class.java]
    }

    @Provides
    fun provideJustInViewModel(
        repository: JustInRepository
    ): JustInViewModel {
        val factory = ViewModelFactory {
            JustInViewModel(repository)
        }
        return ViewModelProvider(fragment, factory)[JustInViewModel::class.java]
    }

    @Provides
    fun provideViewPagerViewModel(
        dbRepository: DatabaseRepository,
    ): ViewPagerViewModel {
        val factory = ViewModelFactory { ViewPagerViewModel(dbRepository) }
        return ViewModelProvider(fragment, factory)[ViewPagerViewModel::class.java]
    }

    @Provides
    fun provideSearchViewModel(
        urlTransformer: UrlTransformer,
        resources: Resources,
        searchRepository: SearchRepository,
        UIBuilder: UIBuilder,
    ): SearchViewModel {
        val factory = ViewModelFactory {
            SearchViewModel(
                urlTransformer,
                resources,
                searchRepository,
                UIBuilder
            )
        }
        return ViewModelProvider(fragment, factory)[SearchViewModel::class.java]
    }

    @Provides
    fun provideFanfictionOpener(): FanfictionOpener = FanfictionOpener(fragment.requireContext())

    @Provides
    fun provideOptionsViewModel(
        resources: Resources,
        databaseRepository: DatabaseRepository,
        downloaderRepository: DownloaderRepository,
        pdfBuilder: PdfBuilder,
        epubBuilder: EpubBuilder,
    ): OptionsViewModel {
        val factory = ViewModelFactory {
            OptionsViewModel(
                resources = resources,
                dbRepository = databaseRepository,
                apiRepository = downloaderRepository,
                pdfBuilder = pdfBuilder,
                epubBuilder = epubBuilder
            )
        }
        return ViewModelProvider(fragment, factory)[OptionsViewModel::class.java]
    }

    @Provides
    fun provideOptionsController(
        optionsViewModel: OptionsViewModel,
        fanfictionOpener: FanfictionOpener,
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
