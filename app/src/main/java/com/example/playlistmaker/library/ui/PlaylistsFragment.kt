package com.example.playlistmaker.library.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.library.domain.Playlist
import com.example.playlistmaker.library.domain.PlaylistState
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment: Fragment() {

    companion object{
        fun newInstance(): PlaylistsFragment {
            return PlaylistsFragment()
        }
    }

    lateinit var playlistAdapter: PlaylistAdapter
    private val playlists = ArrayList<Playlist>()
    private val viewModel: PlaylistsViewModel by viewModel<PlaylistsViewModel>()
    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlaylistsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        playlistAdapter = PlaylistAdapter(playlists)
        binding.playlistRecyclerview.adapter = playlistAdapter
        binding.newPlaylist.setOnClickListener { findNavController().navigate(R.id.action_libraryFragment_to_playlistCreateFragment) }
    }

    private fun render(state: PlaylistState) {
        when (state) {
            is PlaylistState.Empty -> showEmptyState()
            is PlaylistState.Content -> showContentState(state.playlists)
        }
    }


    private fun showEmptyState() {
        with(binding) {
            nothingFoundImg.isVisible = true
            NoPlayLists.isVisible = true
            playlistRecyclerview.isVisible = false
        }
    }

    private fun showContentState(playlistList: List<Playlist>) {
        with(binding) {
            nothingFoundImg.isVisible = false
            NoPlayLists.isVisible = false
            playlistRecyclerview.isVisible = true
        }
        playlists.clear()
        playlists.addAll(playlistList)
        playlistAdapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
