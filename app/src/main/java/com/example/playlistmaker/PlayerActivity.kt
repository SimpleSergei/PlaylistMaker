package com.example.playlistmaker

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {
    private var _binding: ActivityPlayerBinding? = null
    private val binding get() = _binding
        ?: throw IllegalStateException("Binding for player activity must not be null")
    override fun onCreate(savedInstanceState: Bundle?) {
        val track = Gson().fromJson(intent.getStringExtra("selected_track"), Track::class.java)
        super.onCreate(savedInstanceState)
        _binding = ActivityPlayerBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.player)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.backBtn.setOnClickListener { finish() }

        Glide.with(binding.coverTrack)
            .load(track.artworkUrl100.replaceAfterLast("/", "512x512bb.jpg"))
            .placeholder(R.drawable.cover_mockup)
            .centerCrop()
            .transform(RoundedCorners(10))
            .into(binding.coverTrack)

        if (track.collectionName.isEmpty()) binding.groupAlbum.visibility = View.GONE

        with(binding){
            trackName.text = track.trackName
            artistName.text = track.artistName
            trackDuration.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
            trackAlbum.text = track.collectionName
            trackYear.text = track.releaseDate.substring(0,4)
            trackGenre.text = track.primaryGenreName
            trackCountry.text = track.country
        }

    }
}