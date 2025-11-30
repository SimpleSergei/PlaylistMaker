package com.example.playlistmaker.library.ui

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.library.domain.Playlist
import com.example.playlistmaker.utils.TextFormatter

class PlaylistViewHolder(view: View): RecyclerView.ViewHolder(view) {


    private val playlistName: TextView = itemView.findViewById(R.id.view_playlist_name)
    private val tracksCount: TextView = itemView.findViewById(R.id.view_tracks_in_playlist)
    private val playlistCover: ImageView = itemView.findViewById(R.id.view_playlist_cover)

    fun bind(playlist: Playlist){
        playlistName.text = playlist.playlistName
        tracksCount.text = TextFormatter.tracksCountFormat(playlist.tracksCount)

        Glide.with(itemView)
            .load(playlist.playlistCoverPath)
            .placeholder(R.drawable.playlist_cover_mockup)
            .into(playlistCover)
    }

}