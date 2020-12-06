package fr.ffnet.downloader.fanfiction.chapters.injection

import dagger.Subcomponent
import fr.ffnet.downloader.fanfiction.chapters.FanfictionDetailsChaptersFragment

@Subcomponent(
    modules = [
        FanfictionDetailsChaptersModule::class
    ]
)
interface FanfictionDetailsChaptersComponent {
    fun inject(fragment: FanfictionDetailsChaptersFragment)
}
