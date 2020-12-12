package fr.ffnet.downloader.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.observe
import fr.ffnet.downloader.R
import fr.ffnet.downloader.common.MainApplication
import fr.ffnet.downloader.models.SettingType.DEFAULT_SEARCH_ALL
import fr.ffnet.downloader.models.SettingType.DEFAULT_SEARCH_AUTHORS
import fr.ffnet.downloader.models.SettingType.DEFAULT_SEARCH_STORIES
import fr.ffnet.downloader.models.SettingType.EPUB_EXPORT
import fr.ffnet.downloader.models.SettingType.JUST_IN_RECENTLY_PUBLISHED
import fr.ffnet.downloader.models.SettingType.JUST_IN_RECENTLY_UPDATED
import fr.ffnet.downloader.models.SettingType.JUST_IN_SHOW_SECTION
import fr.ffnet.downloader.models.SettingType.MOBI_EXPORT
import fr.ffnet.downloader.models.SettingType.PDF_EXPORT
import fr.ffnet.downloader.settings.injection.SettingsModule
import kotlinx.android.synthetic.main.activity_settings.*
import javax.inject.Inject

class SettingsActivity : AppCompatActivity() {

    @Inject lateinit var settingsViewModel: SettingsViewModel

    companion object {
        fun newIntent(context: Context): Intent = Intent(
            context,
            SettingsActivity::class.java
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        MainApplication
            .getComponent(this)
            .plus(SettingsModule(this))
            .inject(this)

        closeImageView.setOnClickListener {
            finish()
        }

        observe()
        bindFormatSection()
        bindSearchSection()
        bindJustInSection()
    }

    private fun observe() {
        settingsViewModel.settingList.observe(this) { settingList ->
            settingList.map { setting ->
                when (setting.type) {
                    PDF_EXPORT -> pdfExportSwitchCompat.isChecked = setting.isEnabled
                    EPUB_EXPORT -> epubExportSwitchCompat.isChecked = setting.isEnabled
                    MOBI_EXPORT -> mobiExportSwitchCompat.isChecked = setting.isEnabled

                    DEFAULT_SEARCH_ALL -> searchTypeAllRadioButton.isChecked = setting.isEnabled
                    DEFAULT_SEARCH_AUTHORS -> searchTypeAuthorRadioButton.isChecked = setting.isEnabled
                    DEFAULT_SEARCH_STORIES -> searchTypeStoryRadioButton.isChecked = setting.isEnabled

                    JUST_IN_SHOW_SECTION -> justInShowSwitchCompat.isChecked = setting.isEnabled
                    JUST_IN_RECENTLY_PUBLISHED -> justInTypePublishedRadioButton.isChecked = setting.isEnabled
                    JUST_IN_RECENTLY_UPDATED -> justInTypeUpdatedRadioButton.isChecked = setting.isEnabled
                }
            }
        }
    }

    private fun bindFormatSection() {
        pdfExportSwitchCompat.setOnCheckedChangeListener { _, isChecked ->
            settingsViewModel.setChecked(PDF_EXPORT, isChecked)
        }
        epubExportSwitchCompat.setOnCheckedChangeListener { _, isChecked ->
            settingsViewModel.setChecked(EPUB_EXPORT, isChecked)
        }
        mobiExportSwitchCompat.setOnCheckedChangeListener { _, isChecked ->
            settingsViewModel.setChecked(MOBI_EXPORT, isChecked)
        }
    }

    private fun bindSearchSection() {
        searchTypeAllRadioButton.setOnCheckedChangeListener { _, isChecked ->
            settingsViewModel.setChecked(DEFAULT_SEARCH_ALL, isChecked)
            settingsViewModel.setChecked(DEFAULT_SEARCH_AUTHORS, isChecked.not())
            settingsViewModel.setChecked(DEFAULT_SEARCH_STORIES, isChecked.not())
        }
        searchTypeAuthorRadioButton.setOnCheckedChangeListener { _, isChecked ->
            settingsViewModel.setChecked(DEFAULT_SEARCH_ALL, isChecked.not())
            settingsViewModel.setChecked(DEFAULT_SEARCH_AUTHORS, isChecked)
            settingsViewModel.setChecked(DEFAULT_SEARCH_STORIES, isChecked.not())
        }
        searchTypeStoryRadioButton.setOnCheckedChangeListener { _, isChecked ->
            settingsViewModel.setChecked(DEFAULT_SEARCH_ALL, isChecked.not())
            settingsViewModel.setChecked(DEFAULT_SEARCH_AUTHORS, isChecked.not())
            settingsViewModel.setChecked(DEFAULT_SEARCH_STORIES, isChecked)
        }
    }

    private fun bindJustInSection() {
        justInShowSwitchCompat.setOnCheckedChangeListener { _, isChecked ->
            settingsViewModel.setChecked(JUST_IN_SHOW_SECTION, isChecked)
        }
        justInTypePublishedRadioButton.setOnCheckedChangeListener { _, isChecked ->
            settingsViewModel.setChecked(JUST_IN_RECENTLY_PUBLISHED, isChecked)
            settingsViewModel.setChecked(JUST_IN_RECENTLY_UPDATED, isChecked.not())
        }
        justInTypeUpdatedRadioButton.setOnCheckedChangeListener { _, isChecked ->
            settingsViewModel.setChecked(JUST_IN_RECENTLY_PUBLISHED, isChecked.not())
            settingsViewModel.setChecked(JUST_IN_RECENTLY_UPDATED, isChecked)
        }
    }
}
