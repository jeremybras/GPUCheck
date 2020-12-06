package fr.ffnet.downloader.fanfiction.reviews.injection

import dagger.Subcomponent
import fr.ffnet.downloader.fanfiction.reviews.FanfictionDetailsReviewsFragment

@Subcomponent(
    modules = [
        FanfictionDetailsReviewsModule::class
    ]
)
interface FanfictionDetailsReviewsComponent {
    fun inject(fragment: FanfictionDetailsReviewsFragment)
}
