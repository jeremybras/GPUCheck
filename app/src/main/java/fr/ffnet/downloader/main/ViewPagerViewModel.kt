package fr.ffnet.downloader.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import fr.ffnet.downloader.repository.DatabaseRepository

class ViewPagerViewModel(
    private val databaseRepository: DatabaseRepository,
) : ViewModel() {

    fun hasSyncedStories(): LiveData<Boolean> {
        return Transformations.map(databaseRepository.getSyncedFanfictions()) {
            it.isNotEmpty()
        }
    }
}
