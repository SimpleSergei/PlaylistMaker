package com.example.playlistmaker.library.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityLibraryBinding
import com.google.android.material.tabs.TabLayoutMediator

class LibraryActivity : AppCompatActivity() {

    private var _binding: ActivityLibraryBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding for library activity must not be null")

    private lateinit var tabMediator: TabLayoutMediator

    override fun onCreate(savedInstanceState: Bundle?) {
        _binding = ActivityLibraryBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.library)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.viewpager.adapter = PagerAdapter(supportFragmentManager, lifecycle)
        tabMediator = TabLayoutMediator(binding.tabLayout, binding.viewpager) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.favorite_tracks)
                1 -> tab.text = getString(R.string.playlists)
            }
        }
        tabMediator.attach()
        binding.backBtn.setOnClickListener { finish() }
    }

    override fun onDestroy() {
        super.onDestroy()
        tabMediator.detach()
    }
}