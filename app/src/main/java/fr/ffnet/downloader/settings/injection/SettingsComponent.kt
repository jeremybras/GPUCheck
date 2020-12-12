package fr.ffnet.downloader.settings.injection

import dagger.Subcomponent
import fr.ffnet.downloader.settings.SettingsActivity

@Subcomponent(
    modules = [
        SettingsModule::class
    ]
)
interface SettingsComponent {
    fun inject(activity: SettingsActivity)
}
