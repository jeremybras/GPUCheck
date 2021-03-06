package fr.ffnet.downloader.common

import dagger.Component
import fr.ffnet.downloader.author.injection.AuthorComponent
import fr.ffnet.downloader.author.injection.AuthorModule
import fr.ffnet.downloader.fanfiction.chapters.injection.FanfictionDetailsChaptersComponent
import fr.ffnet.downloader.fanfiction.chapters.injection.FanfictionDetailsChaptersModule
import fr.ffnet.downloader.fanfiction.injection.FanfictionComponent
import fr.ffnet.downloader.fanfiction.injection.FanfictionModule
import fr.ffnet.downloader.fanfiction.reviews.injection.FanfictionDetailsReviewsComponent
import fr.ffnet.downloader.fanfiction.reviews.injection.FanfictionDetailsReviewsModule
import fr.ffnet.downloader.fanfiction.summary.injection.FanfictionDetailsSummaryComponent
import fr.ffnet.downloader.fanfiction.summary.injection.FanfictionDetailsSummaryModule
import fr.ffnet.downloader.main.injection.ViewPagerComponent
import fr.ffnet.downloader.main.injection.ViewPagerModule
import fr.ffnet.downloader.main.search.injection.SearchComponent
import fr.ffnet.downloader.main.search.injection.SearchModule
import fr.ffnet.downloader.main.synced.authors.injection.SyncedAuthorsComponent
import fr.ffnet.downloader.main.synced.authors.injection.SyncedAuthorsModule
import fr.ffnet.downloader.main.synced.stories.injection.SyncedStoriesComponent
import fr.ffnet.downloader.main.synced.stories.injection.SyncedStoriesModule
import fr.ffnet.downloader.repository.DownloaderWorker
import fr.ffnet.downloader.repository.RepositoryModule
import fr.ffnet.downloader.settings.injection.SettingsComponent
import fr.ffnet.downloader.settings.injection.SettingsModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        MainModule::class,
        NetworkModule::class,
        RepositoryModule::class
    ]
)
interface MainComponent {

    fun inject(application: MainApplication)
    fun inject(downloaderWorker: DownloaderWorker)

    fun plus(searchModule: SearchModule): SearchComponent
    fun plus(syncedModule: SyncedStoriesModule): SyncedStoriesComponent
    fun plus(syncedModule: SyncedAuthorsModule): SyncedAuthorsComponent
    fun plus(viewPagerModule: ViewPagerModule): ViewPagerComponent
    fun plus(settingsModule: SettingsModule): SettingsComponent

    fun plus(module: FanfictionModule): FanfictionComponent
    fun plus(module: AuthorModule): AuthorComponent

    fun plus(module: FanfictionDetailsSummaryModule): FanfictionDetailsSummaryComponent
    fun plus(module: FanfictionDetailsChaptersModule): FanfictionDetailsChaptersComponent
    fun plus(module: FanfictionDetailsReviewsModule): FanfictionDetailsReviewsComponent
}
