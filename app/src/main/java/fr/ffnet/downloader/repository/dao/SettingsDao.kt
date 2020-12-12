package fr.ffnet.downloader.repository.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import fr.ffnet.downloader.repository.entities.SettingEntity

@Dao
interface SettingsDao {

    @Query("SELECT * FROM settingentity")
    fun getAll(): LiveData<List<SettingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAll(settingEntityList: List<SettingEntity>)
}
