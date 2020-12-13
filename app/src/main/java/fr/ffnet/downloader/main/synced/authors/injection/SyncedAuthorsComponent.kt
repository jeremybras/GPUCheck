package fr.ffnet.downloader.main.synced.authors.injection

import dagger.Subcomponent
import fr.ffnet.downloader.main.synced.authors.SyncedAuthorsFragment

@Subcomponent(
    modules = [
        SyncedAuthorsModule::class
    ]
)
interface SyncedAuthorsComponent {
    fun inject(fragment: SyncedAuthorsFragment)
}
