package com.example.playlistmaker.settings.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import com.example.playlistmaker.settings.domain.SettingsEvent
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SettingsViewModel by viewModel<SettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (viewModel.getThemeSettings()) {
            binding.themeSwitcher.isChecked = true
        }

        openNavigationActivities()

        binding.themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            viewModel.switchTheme()
        }

        binding.shareApp.setOnClickListener {
            viewModel.shareApp()
        }

        binding.support.setOnClickListener {
            viewModel.openSupport()
        }

        binding.license.setOnClickListener {
            viewModel.openTerms()
        }
    }

    private fun openNavigationActivities() {
        viewModel.observeNavigationEvent().observe(viewLifecycleOwner) { event ->
            when (event) {
                is SettingsEvent.ShareApp -> startActivity(Intent.createChooser(event.intent, null))
                is SettingsEvent.OpenTerms -> startActivity(
                    Intent.createChooser(
                        event.intent,
                        null
                    )
                )

                is SettingsEvent.OpenSupport -> startActivity(event.intent)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
