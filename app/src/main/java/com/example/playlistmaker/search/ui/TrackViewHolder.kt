package com.example.playlistmaker.search.ui

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.Track
import java.text.SimpleDateFormat
import java.util.Locale

class TrackViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val songName: TextView = itemView.findViewById(R.id.songName)
    private val artistName: TextView = itemView.findViewById(R.id.artistName)
    private val trackDuration: TextView = itemView.findViewById(R.id.trackDuration)
    private val trackCover: ImageView = itemView.findViewById(R.id.trackCover)

    fun bind(track: Track) {
        songName.text = track.trackName
        artistName.text = track.artistName
        trackDuration.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
        Glide.with(itemView)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.cover_mockup)
            .centerCrop()
            .transform(RoundedCorners(10))
            .into(trackCover)
    }
}