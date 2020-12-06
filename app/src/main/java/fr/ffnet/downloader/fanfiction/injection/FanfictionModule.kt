package fr.ffnet.downloader.fanfiction.injection

import android.content.res.Resources
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import fr.ffnet.downloader.fanfiction.FanfictionActivity
import fr.ffnet.downloader.fanfiction.FanfictionViewModel
import fr.ffnet.downloader.options.OptionsController
import fr.ffnet.downloader.options.OptionsViewModel
import fr.ffnet.downloader.repository.DatabaseRepository
import fr.ffnet.downloader.repository.DownloaderRepository
import fr.ffnet.downloader.utils.EpubBuilder
import fr.ffnet.downloader.utils.FanfictionOpener
import fr.ffnet.downloader.utils.PdfBuilder
import fr.ffnet.downloader.utils.UIBuilder
import fr.ffnet.downloader.utils.ViewModelFactory

@Module
class FanfictionModule(private val activity: FanfictionActivity) {

    @Provides
    fun provideFanfictionViewModel(
        apiRepository: DownloaderRepository,
        dbRepository: DatabaseRepository,
        UIBuilder: UIBuilder
    ): FanfictionViewModel {
        val factory = ViewModelFactory {
            FanfictionViewModel(
                apiRepository,
                dbRepository,
                UIBuilder
            )
        }
        return ViewModelProvider(activity, factory)[FanfictionViewModel::class.java]
    }

    @Provides
    fun provideFanfictionOpener(): FanfictionOpener = FanfictionOpener(activity)

    @Provides
    fun provideOptionsViewModel(
        resources: Resources,
        databaseRepository: DatabaseRepository,
        downloaderRepository: DownloaderRepository,
        pdfBuilder: PdfBuilder,
        epubBuilder: EpubBuilder
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
        return ViewModelProvider(activity, factory)[OptionsViewModel::class.java]
    }

    @Provides
    fun provideOptionsController(
        optionsViewModel: OptionsViewModel,
        fanfictionOpener: FanfictionOpener
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
