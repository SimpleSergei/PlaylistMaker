package com.example.playlistmaker.library.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.playlists.domain.Playlist

class PlaylistLibraryAdapter(private val playlists: List<Playlist>) :
    RecyclerView.Adapter<PlaylistLibraryViewHolder>() {
    var onPlaylistClickListener: ((Playlist) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistLibraryViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_playlist_library, parent, false)
        return PlaylistLibraryViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: PlaylistLibraryViewHolder,
        position: Int
    ) {
        holder.itemView.setOnClickListener { onPlaylistClickListener?.invoke(playlists[position]) }
        holder.bind(playlists[position])
    }

    override fun getItemCount(): Int {
        return playlists.size
    }
}