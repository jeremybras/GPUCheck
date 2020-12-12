package fr.ffnet.downloader.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.ffnet.downloader.models.Setting
import fr.ffnet.downloader.models.SettingType
import fr.ffnet.downloader.models.SettingType.DEFAULT_SEARCH_ALL
import fr.ffnet.downloader.models.SettingType.DEFAULT_SEARCH_AUTHORS
import fr.ffnet.downloader.models.SettingType.DEFAULT_SEARCH_STORIES
import fr.ffnet.downloader.models.SettingType.EPUB_EXPORT
import fr.ffnet.downloader.models.SettingType.JUST_IN_RECENTLY_PUBLISHED
import fr.ffnet.downloader.models.SettingType.JUST_IN_RECENTLY_UPDATED
import fr.ffnet.downloader.models.SettingType.JUST_IN_SHOW_SECTION
import fr.ffnet.downloader.models.SettingType.MOBI_EXPORT
import fr.ffnet.downloader.models.SettingType.PDF_EXPORT
import fr.ffnet.downloader.repository.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val defaultSettings = listOf(
        Setting(PDF_EXPORT, true),
        Setting(EPUB_EXPORT, true),
        Setting(MOBI_EXPORT, true),
        Setting(DEFAULT_SEARCH_ALL, false),
        Setting(DEFAULT_SEARCH_AUTHORS, false),
        Setting(DEFAULT_SEARCH_STORIES, true),
        Setting(JUST_IN_SHOW_SECTION, true),
        Setting(JUST_IN_RECENTLY_PUBLISHED, false),
        Setting(JUST_IN_RECENTLY_UPDATED, true)
    )

    var settingList: LiveData<List<Setting>>

    init {
        settingList = Transformations.map(settingsRepository.getAllSettings()) { allSettings ->
            if (allSettings.size == defaultSettings.size) {
                allSettings
            } else {
                settingsRepository.saveSettings(defaultSettings)
                defaultSettings
            }
        }
    }

    fun setChecked(settingType: SettingType, isChecked: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            settingsRepository.saveSettings(listOf(Setting(settingType, isChecked)))
        }
    }
}
