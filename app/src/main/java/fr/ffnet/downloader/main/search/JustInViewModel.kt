package fr.ffnet.downloader.main.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.ffnet.downloader.models.JustInUI
import fr.ffnet.downloader.repository.JustInRepository
import fr.ffnet.downloader.utils.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class JustInViewModel(
    private val repository: JustInRepository
) : ViewModel() {

    val justInList: SingleLiveEvent<JustInUI> = SingleLiveEvent()

    fun loadJustInList(type: JustInType) {
        viewModelScope.launch(Dispatchers.IO) {
            val justInResult = repository.loadJustInList(type)
            if (justInResult.isNullOrEmpty()) {
                justInList.postValue(JustInUI.JustInError)
            } else {
                val justInSuccess = JustInUI.JustInSuccess(
                    justInResult.map {
                        JustInUI.JustInUIItem(
                            imageUrl = it.image,
                            storyId = it.id
                        )
                    }
                )
                justInList.postValue(justInSuccess)
            }
        }
    }

    enum class JustInType {
        PUBLISHED, UPDATED
    }
}
