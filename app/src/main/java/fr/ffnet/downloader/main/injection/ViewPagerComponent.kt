package fr.ffnet.downloader.main.injection

import dagger.Subcomponent
import fr.ffnet.downloader.main.MainActivity

@Subcomponent(
    modules = [
        ViewPagerModule::class
    ]
)
interface ViewPagerComponent {
    fun inject(activity: MainActivity)
}
