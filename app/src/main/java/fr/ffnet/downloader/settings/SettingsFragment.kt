package fr.ffnet.downloader.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import fr.ffnet.downloader.R
import fr.ffnet.downloader.common.MainApplication
import fr.ffnet.downloader.models.SettingType
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
import kotlinx.android.synthetic.main.fragment_settings.*
import javax.inject.Inject

class SettingsFragment : Fragment() {

    @Inject lateinit var settingsViewModel: SettingsViewModel

    private var shouldListen = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MainApplication
            .getComponent(requireContext())
            .plus(SettingsModule(this))
            .inject(this)

        observe()
        bindFormatSection()
        bindSearchSection()
        bindJustInSection()
    }

    private fun observe() {
        settingsViewModel.settingList.observe(viewLifecycleOwner) { settingList ->
            shouldListen = false
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
            shouldListen = true
        }
    }

    private fun setChecked(settingType: SettingType, isChecked: Boolean) {
        if (shouldListen) {
            settingsViewModel.setChecked(settingType, isChecked)
        }
    }

    private fun bindFormatSection() {
        pdfExportSwitchCompat.setOnCheckedChangeListener { _, isChecked ->
            setChecked(PDF_EXPORT, isChecked)
        }
        epubExportSwitchCompat.setOnCheckedChangeListener { _, isChecked ->
            setChecked(EPUB_EXPORT, isChecked)
        }
        mobiExportSwitchCompat.setOnCheckedChangeListener { _, isChecked ->
            setChecked(MOBI_EXPORT, isChecked)
        }
    }

    private fun bindSearchSection() {
        searchTypeAllRadioButton.setOnCheckedChangeListener { _, isChecked ->
            setChecked(DEFAULT_SEARCH_ALL, isChecked)
            setChecked(DEFAULT_SEARCH_AUTHORS, isChecked.not())
            setChecked(DEFAULT_SEARCH_STORIES, isChecked.not())
        }
        searchTypeAuthorRadioButton.setOnCheckedChangeListener { _, isChecked ->
            setChecked(DEFAULT_SEARCH_ALL, isChecked.not())
            setChecked(DEFAULT_SEARCH_AUTHORS, isChecked)
            setChecked(DEFAULT_SEARCH_STORIES, isChecked.not())
        }
        searchTypeStoryRadioButton.setOnCheckedChangeListener { _, isChecked ->
            setChecked(DEFAULT_SEARCH_ALL, isChecked.not())
            setChecked(DEFAULT_SEARCH_AUTHORS, isChecked.not())
            setChecked(DEFAULT_SEARCH_STORIES, isChecked)
        }
    }

    private fun bindJustInSection() {
        justInShowSwitchCompat.setOnCheckedChangeListener { _, isChecked ->
            setChecked(JUST_IN_SHOW_SECTION, isChecked)
        }
        justInTypePublishedRadioButton.setOnCheckedChangeListener { _, isChecked ->
            setChecked(JUST_IN_RECENTLY_PUBLISHED, isChecked)
            setChecked(JUST_IN_RECENTLY_UPDATED, isChecked.not())
        }
        justInTypeUpdatedRadioButton.setOnCheckedChangeListener { _, isChecked ->
            setChecked(JUST_IN_RECENTLY_PUBLISHED, isChecked.not())
            setChecked(JUST_IN_RECENTLY_UPDATED, isChecked)
        }
    }
}