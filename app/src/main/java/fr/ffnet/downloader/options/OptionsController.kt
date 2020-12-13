package fr.ffnet.downloader.options

import android.content.Context
import android.os.Environment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.observe
import fr.ffnet.downloader.author.AuthorActivity
import fr.ffnet.downloader.common.FFLogger
import fr.ffnet.downloader.fanfiction.FanfictionActivity
import fr.ffnet.downloader.utils.FanfictionOpener

interface OnFanfictionActionsListener {
    fun onFetchInformation(fanfictionId: String)
    fun onExportPdf(fanfictionId: String)
    fun onExportEpub(fanfictionId: String)
    fun onUnsyncStory(fanfictionId: String)
    fun onSync(fanfictionId: String)
    fun onFetchAuthorInformation(authorId: String)
    fun onUnsyncAuthor(authorId: String)
}

interface ParentListener {
    fun showErrorMessage(message: String)
}

class OptionsController(
    private val context: Context,
    lifecycleOwner: LifecycleOwner,
    private val parentListener: ParentListener,
    private val optionsViewModel: OptionsViewModel,
    private val fanfictionOpener: FanfictionOpener
) : OnFanfictionActionsListener {

    private val absolutePath: String by lazy {
        context.getExternalFilesDir(
            Environment.DIRECTORY_DOCUMENTS
        )?.absolutePath ?: throw IllegalArgumentException()
    }

    init {
        optionsViewModel.getFile.observe(lifecycleOwner) { (fileName, absolutePath) ->
            fanfictionOpener.openFile(fileName, absolutePath)
        }
        optionsViewModel.navigateToStory.observe(lifecycleOwner) { fanfictionId ->
            context.startActivity(
                FanfictionActivity.newIntent(context, fanfictionId)
            )
        }
        optionsViewModel.error.observe(lifecycleOwner) { errorMessage ->
            parentListener.showErrorMessage(errorMessage)
        }
        optionsViewModel.navigateToAuthor.observe(lifecycleOwner) { authorId ->
            context.startActivity(
                AuthorActivity.newIntent(context, authorId)
            )
        }
    }

    override fun onFetchInformation(fanfictionId: String) {
        FFLogger.d(FFLogger.EVENT_KEY, "Opening details for $fanfictionId")
        optionsViewModel.loadFanfictionInfo(fanfictionId)
    }

    override fun onExportPdf(fanfictionId: String) {
        FFLogger.d(FFLogger.EVENT_KEY, "Export PDF for $fanfictionId")
        optionsViewModel.buildPdf(absolutePath, fanfictionId)
    }

    override fun onExportEpub(fanfictionId: String) {
        FFLogger.d(FFLogger.EVENT_KEY, "Export EPUB for $fanfictionId")
        optionsViewModel.buildEpub(absolutePath, fanfictionId)
    }

    override fun onUnsyncStory(fanfictionId: String) {
        FFLogger.d(FFLogger.EVENT_KEY, "Unsync $fanfictionId")
        optionsViewModel.unsyncFanfiction(fanfictionId)
    }

    override fun onSync(fanfictionId: String) {
        FFLogger.d(FFLogger.EVENT_KEY, "Sync $fanfictionId")
        optionsViewModel.onSyncFanfiction(fanfictionId)
    }

    override fun onFetchAuthorInformation(authorId: String) {
        optionsViewModel.loadAuthorInfo(authorId)
    }

    override fun onUnsyncAuthor(authorId: String) {
        optionsViewModel.unsyncAuthor(authorId)
    }
}
