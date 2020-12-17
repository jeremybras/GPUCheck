package fr.ffnet.downloader.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import fr.ffnet.downloader.common.MainApplication
import fr.ffnet.downloader.databinding.FragmentSettingsBinding
import fr.ffnet.downloader.models.SettingType
import fr.ffnet.downloader.models.SettingType.DEFAULT_SEARCH_AUTHORS
import fr.ffnet.downloader.models.SettingType.DEFAULT_SEARCH_STORIES
import fr.ffnet.downloader.models.SettingType.EPUB_EXPORT
import fr.ffnet.downloader.models.SettingType.MOBI_EXPORT
import fr.ffnet.downloader.models.SettingType.PDF_EXPORT
import fr.ffnet.downloader.settings.injection.SettingsModule
import javax.inject.Inject

class SettingsFragment : Fragment() {

    @Inject lateinit var settingsViewModel: SettingsViewModel

    private var shouldListen = false
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
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
    }

    private fun observe() {
        settingsViewModel.settingList.observe(viewLifecycleOwner) { settingList ->
            shouldListen = false
            settingList.map { setting ->
                when (setting.type) {
                    PDF_EXPORT -> binding.pdfExportSwitchCompat.isChecked = setting.isEnabled
                    EPUB_EXPORT -> binding.epubExportSwitchCompat.isChecked = setting.isEnabled
                    MOBI_EXPORT -> binding.mobiExportSwitchCompat.isChecked = setting.isEnabled

                    DEFAULT_SEARCH_AUTHORS -> binding.searchTypeAuthorRadioButton.isChecked = setting.isEnabled
                    DEFAULT_SEARCH_STORIES -> binding.searchTypeStoryRadioButton.isChecked = setting.isEnabled

                    else -> {
                        // Do nothing
                    }
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
        binding.pdfExportSwitchCompat.setOnCheckedChangeListener { _, isChecked ->
            setChecked(PDF_EXPORT, isChecked)
        }
        binding.epubExportSwitchCompat.setOnCheckedChangeListener { _, isChecked ->
            setChecked(EPUB_EXPORT, isChecked)
        }
        binding.mobiExportSwitchCompat.setOnCheckedChangeListener { _, isChecked ->
            setChecked(MOBI_EXPORT, isChecked)
        }
    }

    private fun bindSearchSection() {
        binding.searchTypeAuthorRadioButton.setOnCheckedChangeListener { _, isChecked ->
            setChecked(DEFAULT_SEARCH_AUTHORS, isChecked)
            setChecked(DEFAULT_SEARCH_STORIES, isChecked.not())
        }
        binding.searchTypeStoryRadioButton.setOnCheckedChangeListener { _, isChecked ->
            setChecked(DEFAULT_SEARCH_AUTHORS, isChecked.not())
            setChecked(DEFAULT_SEARCH_STORIES, isChecked)
        }
    }
}
