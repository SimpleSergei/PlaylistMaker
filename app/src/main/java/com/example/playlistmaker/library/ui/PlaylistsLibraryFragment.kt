package com.example.playlistmaker.library.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistsLibraryBinding
import com.example.playlistmaker.library.domain.PlaylistLibraryState
import com.example.playlistmaker.playlists.domain.Playlist
import com.example.playlistmaker.playlists.ui.PlaylistDetailsFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsLibraryFragment : Fragment() {

    companion object{
        fun newInstance(): PlaylistsLibraryFragment {
            return PlaylistsLibraryFragment()
        }
    }

    lateinit var playlistAdapter: PlaylistLibraryAdapter
    private val playlists = ArrayList<Playlist>()
    private val viewModel: PlaylistsLibraryViewModel by viewModel<PlaylistsLibraryViewModel>()
    private var _binding: FragmentPlaylistsLibraryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlaylistsLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        playlistAdapter = PlaylistLibraryAdapter(playlists)
        binding.playlistRecyclerview.adapter = playlistAdapter
        binding.newPlaylist.setOnClickListener { findNavController().navigate(R.id.action_libraryFragment_to_playlistCreateFragment) }
        playlistAdapter.onPlaylistClickListener =
            { playlistforecast -> findNavController().navigate(R.id.action_libraryFragment_to_playlistDetailsFragment,
                PlaylistDetailsFragment.createArgs(playlistforecast.playlistId)) }
    }

    private fun render(state: PlaylistLibraryState) {
        when (state) {
            is PlaylistLibraryState.Empty -> showEmptyState()
            is PlaylistLibraryState.Content -> showContentState(state.playlists)
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
