package fr.ffnet.downloader.author

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import fr.ffnet.downloader.R
import fr.ffnet.downloader.author.injection.AuthorModule
import fr.ffnet.downloader.common.MainApplication
import fr.ffnet.downloader.databinding.ActivityAuthorBinding
import fr.ffnet.downloader.main.search.SearchListAdapter
import fr.ffnet.downloader.options.OptionsController
import fr.ffnet.downloader.options.ParentListener
import javax.inject.Inject

class AuthorActivity : AppCompatActivity(), ParentListener {

    @Inject lateinit var authorViewModel: AuthorViewModel
    @Inject lateinit var optionsController: OptionsController
    @Inject lateinit var picasso: Picasso

    private lateinit var binding: ActivityAuthorBinding

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

        binding = ActivityAuthorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authorViewModel.loadAuthorInfo(authorId)
        setListeners()
        setObservers()
    }

    override fun showErrorMessage(message: String) {
        Snackbar.make(binding.containerView, message, Snackbar.LENGTH_LONG).show()
    }

    private fun setListeners() {
        binding.menuImageView.frame = 59
        binding.menuImageView.setOnClickListener {
            finish()
        }
    }

    private fun setObservers() {
        authorViewModel.author.observe(this) { author ->

            picasso
                .load(author.imageUrl)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(binding.authorImageView)

            binding.titleTextView.text = author.title
            binding.nbStoriesDateTextView.text = author.nbStories
            binding.nbFavoritesDateTextView.text = author.nbFavorites
        }
        authorViewModel.storyList.observe(this) { storyList ->
            binding.storiesRecyclerView.adapter = SearchListAdapter(
                picasso,
                optionsController
            ).apply {
                searchResultList = storyList
            }
        }
    }
}
