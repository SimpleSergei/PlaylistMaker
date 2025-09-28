package com.example.playlistmaker.settings.ui


import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.databinding.ActivitySettingsBinding


class SettingsActivity : AppCompatActivity() {
    private var _binding: ActivitySettingsBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding for search activity must not be null")
    private lateinit var viewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        _binding = ActivitySettingsBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settings)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val sharingInteractor = Creator.provideSharingInteractor()
        val settingsInteractor = Creator.provideSettingsInteractor()
        viewModel = ViewModelProvider(
            this,
            SettingsViewModel.getFactory(sharingInteractor, settingsInteractor)
        ).get(
            SettingsViewModel::class.java
        )

        if (viewModel.getThemeSettings()) {
            binding.themeSwitcher.isChecked = true
        }

        binding.themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            viewModel.switchTheme()
        }

        binding.backBtn.setOnClickListener {
            finish()
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
}