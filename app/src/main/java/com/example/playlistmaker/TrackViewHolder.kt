package com.example.playlistmaker

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TrackViewHolder (itemView:View) : RecyclerView.ViewHolder(itemView) {
    private val songName: TextView = itemView.findViewById(R.id.SongName)
    private val artistName: TextView = itemView.findViewById(R.id.ArtistName)
    private val trackDuration: TextView = itemView.findViewById(R.id.TrackDuration)
    private val trackCover: ImageView = itemView.findViewById(R.id.TrackCover)

    fun bind(track:Track) {
        songName.text = track.trackName
        artistName.text = track.artistName
        trackDuration.text = track.trackTime
        Glide.with(itemView)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.no_internet)
            .centerCrop()
            .transform(RoundedCorners(10))
            .into(trackCover)
    }
}