package com.example.playlistmaker.ui.player

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {
    private var _binding: ActivityPlayerBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding for player activity must not be null")

    private var playerState = STATE_DEFAULT
    private var mediaPlayer = MediaPlayer()
    private var mainThreadHandler: Handler? = null
    private lateinit var track: Track

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityPlayerBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.player)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        track = Gson().fromJson(intent.getStringExtra("selected_track"), Track::class.java)
        mainThreadHandler = Handler(Looper.getMainLooper())

        preparePlayer()

        binding.backBtn.setOnClickListener { finish() }
        binding.playBtn.setOnClickListener { playbackControl() }

        Glide.with(binding.coverTrack)
            .load(track.artworkUrl100.replaceAfterLast("/", "512x512bb.jpg"))
            .placeholder(R.drawable.cover_mockup)
            .centerCrop()
            .transform(RoundedCorners(10))
            .into(binding.coverTrack)

        if (track.collectionName.isEmpty()) binding.groupAlbum.visibility = View.GONE

        with(binding) {
            trackName.text = track.trackName
            artistName.text = track.artistName
            trackDuration.text =
                SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
            trackAlbum.text = track.collectionName
            trackYear.text = track.releaseDate.substring(0, 4)
            trackGenre.text = track.primaryGenreName
            trackCountry.text = track.country
        }

    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        mediaPlayer.release()
        mainThreadHandler?.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(track.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            binding.playBtn.setImageResource(R.drawable.play_button)
            playerState = STATE_PREPARED
            binding.trackTime.text = "00:00"
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        binding.playBtn.setImageResource(R.drawable.pause_button)
        playerState = STATE_PLAYING
        startTimer()
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        binding.playBtn.setImageResource(R.drawable.play_button)
        playerState = STATE_PAUSED

    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }

            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    private fun startTimer() {
        mainThreadHandler?.post(createUpdateTimerTask())
    }

    private fun createUpdateTimerTask(): Runnable {
        return object : Runnable {
            override fun run() {
                if (playerState == STATE_PLAYING) {
                    binding.trackTime.text = SimpleDateFormat(
                        "mm:ss",
                        Locale.getDefault()
                    ).format(mediaPlayer.currentPosition)
                    mainThreadHandler?.postDelayed(this, REFRESH_TIMER_DELAY)
                }
            }
        }
    }

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val REFRESH_TIMER_DELAY = 333L
    }
}