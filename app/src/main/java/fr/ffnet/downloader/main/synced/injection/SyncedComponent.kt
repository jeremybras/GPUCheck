package fr.ffnet.downloader.main.synced.injection

import dagger.Subcomponent
import fr.ffnet.downloader.main.synced.SyncedFragment

@Subcomponent(
    modules = [
        SyncedModule::class
    ]
)
interface SyncedComponent {
    fun inject(fragment: SyncedFragment)
}
