package fr.ffnet.downloader.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import fr.ffnet.downloader.models.Setting
import fr.ffnet.downloader.models.SettingType
import fr.ffnet.downloader.repository.dao.SettingsDao
import fr.ffnet.downloader.repository.entities.SettingEntity

class SettingsRepository(
    private val settingsDao: SettingsDao
) {

    fun getAllSettings(): LiveData<List<Setting>> {
        return Transformations.map(settingsDao.getAll()) { settingList ->
            settingList.map { setting ->
                Setting(
                    type = SettingType.valueOf(setting.type),
                    isEnabled = setting.isEnabled
                )
            }
        }
    }

    fun saveSettings(settingList: List<Setting>) {
        val settingEntityList = settingList.map { setting ->
            SettingEntity(
                type = setting.type.name,
                isEnabled = setting.isEnabled
            )
        }
        settingsDao.saveAll(settingEntityList)
    }
}
