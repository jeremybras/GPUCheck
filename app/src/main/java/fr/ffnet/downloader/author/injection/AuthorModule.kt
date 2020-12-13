package fr.ffnet.downloader.author.injection

import android.content.res.Resources
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import fr.ffnet.downloader.author.AuthorActivity
import fr.ffnet.downloader.author.AuthorViewModel
import fr.ffnet.downloader.options.OptionsController
import fr.ffnet.downloader.options.OptionsViewModel
import fr.ffnet.downloader.repository.AuthorRepository
import fr.ffnet.downloader.repository.DatabaseRepository
import fr.ffnet.downloader.repository.DownloaderRepository
import fr.ffnet.downloader.utils.EpubBuilder
import fr.ffnet.downloader.utils.FanfictionOpener
import fr.ffnet.downloader.utils.PdfBuilder
import fr.ffnet.downloader.utils.UIBuilder
import fr.ffnet.downloader.utils.ViewModelFactory

@Module
class AuthorModule(private val activity: AuthorActivity) {

    @Provides
    fun provideAuthorViewModel(
        resources: Resources,
        databaseRepository: DatabaseRepository,
        authorRepository: AuthorRepository,
        uiBuilder: UIBuilder
    ): AuthorViewModel {
        val factory = ViewModelFactory {
            AuthorViewModel(
                resources = resources,
                databaseRepository = databaseRepository,
                authorRepository = authorRepository,
                uiBuilder = uiBuilder
            )
        }
        return ViewModelProvider(activity, factory)[AuthorViewModel::class.java]
    }

    @Provides
    fun provideFanfictionOpener(): FanfictionOpener = FanfictionOpener(activity)

    @Provides
    fun provideOptionsViewModel(
        resources: Resources,
        databaseRepository: DatabaseRepository,
        downloaderRepository: DownloaderRepository,
        authorRepository: AuthorRepository,
        pdfBuilder: PdfBuilder,
        epubBuilder: EpubBuilder,
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
        return ViewModelProvider(activity, factory)[OptionsViewModel::class.java]
    }

    @Provides
    fun provideOptionsController(
        optionsViewModel: OptionsViewModel,
        fanfictionOpener: FanfictionOpener,
    ): OptionsController {
        return OptionsController(
            context = activity,
            lifecycleOwner = activity,
            parentListener = activity,
            optionsViewModel = optionsViewModel,
            fanfictionOpener = fanfictionOpener
        )
    }
}
