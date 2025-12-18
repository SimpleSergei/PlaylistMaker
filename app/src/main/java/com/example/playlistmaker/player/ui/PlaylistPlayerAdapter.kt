package com.example.playlistmaker.player.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.playlists.domain.Playlist


class PlaylistPlayerAdapter(private val playlists: List<Playlist>) :
    RecyclerView.Adapter<PlaylistPlayerViewHolder>() {
    var onPlaylistClickListener: ((Playlist) -> Unit)? = null
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlaylistPlayerViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_playlist_selection, parent, false)
        return PlaylistPlayerViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: PlaylistPlayerViewHolder,
        position: Int
    ) {
        holder.bind(playlists[position])
        holder.itemView.setOnClickListener { onPlaylistClickListener?.invoke(playlists[position]) }
    }

    override fun getItemCount(): Int {
        return playlists.size
    }
}