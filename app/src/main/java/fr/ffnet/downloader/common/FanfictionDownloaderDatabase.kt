package fr.ffnet.downloader.common

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import fr.ffnet.downloader.repository.dao.FanfictionDao
import fr.ffnet.downloader.repository.dao.AuthorDao
import fr.ffnet.downloader.repository.dao.SettingsDao
import fr.ffnet.downloader.repository.entities.AuthorEntity
import fr.ffnet.downloader.repository.entities.ChapterEntity
import fr.ffnet.downloader.repository.entities.FanfictionEntity
import fr.ffnet.downloader.repository.entities.ProfileFanfictionEntity
import fr.ffnet.downloader.repository.entities.ReviewEntity
import fr.ffnet.downloader.repository.entities.SettingEntity
import fr.ffnet.downloader.utils.Converters

@Database(
    entities = [
        FanfictionEntity::class,
        ChapterEntity::class,
        AuthorEntity::class,
        ReviewEntity::class,
        ProfileFanfictionEntity::class,
        SettingEntity::class
    ],
    version = 5,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class FanfictionDownloaderDatabase : RoomDatabase() {
    abstract fun fanfictionDao(): FanfictionDao
    abstract fun authorDao(): AuthorDao
    abstract fun settingsDao(): SettingsDao
}
