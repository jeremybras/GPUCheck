package fr.ffnet.downloader.main.synced.authors

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import fr.ffnet.downloader.models.SyncedUIItem
import fr.ffnet.downloader.repository.AuthorRepository
import fr.ffnet.downloader.utils.UIBuilder

class SyncedAuthorsViewModel(
    private val uiBuilder: UIBuilder,
    private val authorRepository: AuthorRepository
) : ViewModel() {

    lateinit var authors: LiveData<List<SyncedUIItem>>

    fun loadAuthorsStories() {
        authors = Transformations.map(authorRepository.loadSyncedAuthors()) { authorList ->
            authorList.map { author ->
                uiBuilder.buildAuthorUI(author.name, author.nbStories, author.nbFavorites)
            }
        }
    }
}
