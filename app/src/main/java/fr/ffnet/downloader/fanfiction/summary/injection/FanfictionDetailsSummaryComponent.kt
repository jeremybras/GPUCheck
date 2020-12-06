package fr.ffnet.downloader.fanfiction.summary.injection

import dagger.Subcomponent
import fr.ffnet.downloader.fanfiction.summary.FanfictionDetailsSummaryFragment

@Subcomponent(
    modules = [
        FanfictionDetailsSummaryModule::class
    ]
)
interface FanfictionDetailsSummaryComponent {
    fun inject(fragment: FanfictionDetailsSummaryFragment)
}
