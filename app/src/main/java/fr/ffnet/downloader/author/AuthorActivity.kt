package fr.ffnet.downloader.author

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.observe
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import fr.ffnet.downloader.R
import fr.ffnet.downloader.author.injection.AuthorModule
import fr.ffnet.downloader.common.MainApplication
import fr.ffnet.downloader.main.search.SearchListAdapter
import fr.ffnet.downloader.options.OptionsController
import fr.ffnet.downloader.options.ParentListener
import kotlinx.android.synthetic.main.activity_author.*
import kotlinx.android.synthetic.main.fragment_search.*
import javax.inject.Inject

class AuthorActivity : AppCompatActivity(), ParentListener {

    @Inject lateinit var authorViewModel: AuthorViewModel
    @Inject lateinit var optionsController: OptionsController
    @Inject lateinit var picasso: Picasso

    companion object {

        private const val EXTRA_AUTHOR_ID = "EXTRA_AUTHOR_ID"

        fun newIntent(context: Context, authorId: String) = Intent(
            context,
            AuthorActivity::class.java
        ).apply {
            putExtra(EXTRA_AUTHOR_ID, authorId)
        }
    }

    private val authorId by lazy { intent.getStringExtra(EXTRA_AUTHOR_ID) ?: "" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MainApplication.getComponent(this)
            .plus(AuthorModule(this))
            .inject(this)

        setContentView(R.layout.activity_author)

        authorViewModel.loadAuthorInfo(authorId)
        setListeners()
        setObservers()
    }

    override fun showErrorMessage(message: String) {
        Snackbar.make(searchContainer, message, Snackbar.LENGTH_LONG).show()
    }

    private fun setListeners() {
        menuImageView.frame = 59
        menuImageView.setOnClickListener {
            finish()
        }
    }

    private fun setObservers() {
        authorViewModel.author.observe(this) { author ->
            titleTextView.text = author.title
            nbStoriesDateTextView.text = author.nbStories
            nbFavoritesDateTextView.text = author.nbFavorites
        }
        authorViewModel.storyList.observe(this) { storyList ->
            storiesRecyclerView.adapter = SearchListAdapter(picasso, optionsController).apply {
                searchResultList = storyList
            }
        }
    }
}
