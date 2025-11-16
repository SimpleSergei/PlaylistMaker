package com.example.playlistmaker.library.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentFavTracksBinding
import com.example.playlistmaker.library.domain.FavoriteTracksState
import com.example.playlistmaker.player.ui.PlayerFragment
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.search.ui.TrackAdapter
import com.example.playlistmaker.utils.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavTracksFragment : Fragment() {
    private lateinit var onTrackClickDebounce: (Track) -> Unit
    private val tracksFavorite = ArrayList<Track>()
    lateinit var tracksAdapter: TrackAdapter

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        fun newInstance(): FavTracksFragment {
            return FavTracksFragment()
        }
    }

    private val viewModel: FavTracksViewModel by viewModel<FavTracksViewModel>()

    private var _binding: FragmentFavTracksBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tracksAdapter = TrackAdapter(tracksFavorite)
        binding.recyclerView.adapter = tracksAdapter

        onTrackClickDebounce = debounce(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { track ->
            findNavController().navigate(
                R.id.action_libraryFragment_to_playerFragment,
                PlayerFragment.createArgs(track)
            )
        }

        tracksAdapter.onTrackClickListener = { trackForecast ->
            onTrackClickDebounce(trackForecast)
        }

        viewModel.observeState().observe(viewLifecycleOwner) { state ->
            when (state) {
                is FavoriteTracksState.Empty -> showEmptyState()
                is FavoriteTracksState.Content -> showFavoriteTracks(state.tracks)
            }
        }
    }

    private fun showEmptyState() {
        with(binding) {
            recyclerView.isVisible = false
            nothingFoundImg.isVisible = true
            NoFavTracks.isVisible = true
        }
    }

    private fun showFavoriteTracks(tracks: List<Track>) {
        tracksFavorite.clear()
        tracksFavorite.addAll(tracks)
        tracksAdapter.notifyDataSetChanged()
        binding.recyclerView.isVisible = true
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}