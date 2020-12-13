package fr.ffnet.downloader.main.synced.stories.injection

import dagger.Subcomponent
import fr.ffnet.downloader.main.synced.stories.SyncedStoriesFragment

@Subcomponent(
    modules = [
        SyncedStoriesModule::class
    ]
)
interface SyncedStoriesComponent {
    fun inject(fragment: SyncedStoriesFragment)
}
