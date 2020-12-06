package fr.ffnet.downloader.options

import android.content.Context
import android.os.Environment
import androidx.lifecycle.LifecycleOwner
import fr.ffnet.downloader.common.FFLogger
import fr.ffnet.downloader.fanfiction.FanfictionActivity
import fr.ffnet.downloader.options.OptionsViewModel.SearchError
import fr.ffnet.downloader.utils.FanfictionOpener

interface OnFanfictionActionsListener {
    fun onFetchInformation(fanfictionId: String)
    fun onExportPdf(fanfictionId: String)
    fun onExportEpub(fanfictionId: String)
    fun onUnsync(fanfictionId: String)
    fun onSync(fanfictionId: String)
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
        optionsViewModel.getFile.observe(lifecycleOwner, { (fileName, absolutePath) ->
            fanfictionOpener.openFile(fileName, absolutePath)
        })
        optionsViewModel.navigateToFanfiction.observe(lifecycleOwner, { fanfictionId ->
            context.startActivity(
                FanfictionActivity.intent(
                    context,
                    fanfictionId
                )
            )
        })
        optionsViewModel.error.observe(lifecycleOwner, { searchError ->
            when (searchError) {
                is SearchError.InfoFetchingFailed -> {
                    parentListener.showErrorMessage(searchError.message)
                }
            }
        })
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

    override fun onUnsync(fanfictionId: String) {
        FFLogger.d(FFLogger.EVENT_KEY, "Unsync $fanfictionId")
        optionsViewModel.unsyncFanfiction(fanfictionId)
    }

    override fun onSync(fanfictionId: String) {
        FFLogger.d(FFLogger.EVENT_KEY, "Sync $fanfictionId")
        optionsViewModel.onSyncFanfiction(fanfictionId)
    }
}
