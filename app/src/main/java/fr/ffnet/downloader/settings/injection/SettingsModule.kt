package fr.ffnet.downloader.settings.injection

import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import fr.ffnet.downloader.repository.SettingsRepository
import fr.ffnet.downloader.settings.SettingsActivity
import fr.ffnet.downloader.settings.SettingsViewModel
import fr.ffnet.downloader.utils.ViewModelFactory

@Module
class SettingsModule(private val activity: SettingsActivity) {

    @Provides
    fun provideSettingsViewModel(
        repository: SettingsRepository
    ): SettingsViewModel {
        val factory = ViewModelFactory {
            SettingsViewModel(repository)
        }
        return ViewModelProvider(activity, factory)[SettingsViewModel::class.java]
    }
}
