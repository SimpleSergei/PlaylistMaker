package com.example.playlistmaker.player.ui

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.playlists.domain.Playlist
import com.example.playlistmaker.utils.TextFormatter

class PlaylistPlayerViewHolder(view: View): RecyclerView.ViewHolder(view) {
    private val playlistName: TextView = itemView.findViewById(R.id.playlist_player_name)
    private val tracksCount: TextView = itemView.findViewById(R.id.playlist_player_count)
    private val playlistCover: ImageView = itemView.findViewById(R.id.playlist_player_cover)

    fun bind(playlist: Playlist){
        playlistName.text = playlist.playlistName
        tracksCount.text = TextFormatter.tracksCountFormat(playlist.tracksCount)
        Glide.with(itemView)
            .load(playlist.playlistCoverPath)
            .placeholder(R.drawable.cover_mockup)
            .into(playlistCover)
    }
}
