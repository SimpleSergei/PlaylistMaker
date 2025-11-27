package com.example.playlistmaker.library.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.library.domain.Playlist

class PlaylistAdapter(private val playlists: List<Playlist>) :
    RecyclerView.Adapter<PlaylistViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.playlist_view, parent, false)
        return PlaylistViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: PlaylistViewHolder,
        position: Int
    ) {
        holder.bind(playlists[position])
    }

    override fun getItemCount(): Int {
        return playlists.size
    }
}