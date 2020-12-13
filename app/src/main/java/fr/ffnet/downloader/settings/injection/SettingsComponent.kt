package fr.ffnet.downloader.settings.injection

import dagger.Subcomponent
import fr.ffnet.downloader.settings.SettingsFragment

@Subcomponent(
    modules = [
        SettingsModule::class
    ]
)
interface SettingsComponent {
    fun inject(fragment: SettingsFragment)
}
