package com.example.playlistmaker.playlists.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistDetailsBinding
import com.example.playlistmaker.player.ui.PlayerFragment
import com.example.playlistmaker.playlists.domain.Playlist
import com.example.playlistmaker.playlists.domain.PlaylistDetailsState
import com.example.playlistmaker.playlists.domain.ShareEvent
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.search.ui.TrackAdapter
import com.example.playlistmaker.utils.TextFormatter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlaylistDetailsFragment : Fragment() {
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private var _binding: FragmentPlaylistDetailsBinding? = null
    private val binding get() = _binding!!
    lateinit var tracksAdapter: TrackAdapter
    private val tracks = ArrayList<Track>()
    private val viewModel: PlaylistDetailsViewModel by viewModel<PlaylistDetailsViewModel> {
        parametersOf(
            playlistId
        )
    }
    private val playlistId: Long by lazy {
        requireArguments().getLong(ARGS_PLAYLIST)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlaylistDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheetMenu).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {

                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.isVisible = false
                    }

                    else -> {
                        binding.overlay.isVisible = true
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        viewModel.playlistDetailsState.observe(viewLifecycleOwner) { state ->
            renderState(state)
        }

        tracksAdapter = TrackAdapter(tracks)
        binding.tracksRecyclerView.adapter = tracksAdapter

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            parentFragmentManager.popBackStack()
        }
        binding.backBtn.setOnClickListener { parentFragmentManager.popBackStack() }

        tracksAdapter.onTrackClickListener = { trackForecast ->
            findNavController().navigate(
                R.id.action_playlistDetailsFragment_to_playerFragment,
                PlayerFragment.createArgs(trackForecast)
            )
        }
        tracksAdapter.onTrackLongClickListener = { trackForecast ->
            showDeleteTrackDialog(trackForecast)
        }

        binding.playlistMore.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        binding.playlistShare.setOnClickListener { viewModel.sharePlaylist() }
        binding.textShare.setOnClickListener { viewModel.sharePlaylist() }
        binding.textDelete.setOnClickListener {
            showDeletePlaylistDialog()
        }
        binding.textEdit.setOnClickListener { findNavController().navigate(R.id.action_playlistDetailsFragment_to_playlistEditFragment,
            PlaylistEditFragment.createArgs(playlistId)) }



        viewModel.shareEvent.observe(viewLifecycleOwner) { event ->
            when (event) {
                is ShareEvent.EmptyPlaylist -> showToast(getString(R.string.empty_playlist_message))
                is ShareEvent.SharePlaylist -> startShareIntent(event.intent)
            }
        }
    }

    private fun renderState(state: PlaylistDetailsState) {
        when (state) {
            is PlaylistDetailsState.Empty -> renderPlaylistInfo(state.playlist, 0, 0, emptyList())
            is PlaylistDetailsState.Content -> renderPlaylistInfo(
                state.playlist,
                state.totalDuration,
                state.playlist.tracksCount,
                state.tracks
            )

            is PlaylistDetailsState.Deleted -> {
                findNavController().popBackStack()
            }
        }
    }

    private fun showDeletePlaylistDialog() {
        binding.overlayMenu.isVisible = true
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Удалить плейлист")
            .setMessage("Хотите удалить плейлист?")
            .setPositiveButton("Да") { dialog, _ ->
                dialog.dismiss()
                binding.overlayMenu.isVisible = false
                viewModel.deletePlaylist()
            }
            .setNegativeButton("Нет") { dialog, _ ->
                dialog.dismiss()
                binding.overlayMenu.isVisible = false
            }
            .create()
            .show()
    }

    private fun showDeleteTrackDialog(track: Track) {
        binding.overlay.isVisible = true
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Удаление трека")
            .setMessage("Хотите удалить трек \"${track.trackName}\" из плейлиста?")
            .setPositiveButton("ДА") { dialog, _ ->
                dialog.dismiss()
                binding.overlay.isVisible = false
                viewModel.deleteTrackFromPlaylist(track)
            }
            .setNegativeButton("НЕТ") { dialog, _ ->
                binding.overlay.isVisible = false
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun renderPlaylistInfo(
        playlist: Playlist,
        totalDuration: Long,
        tracksCount: Int,
        tracks: List<Track>
    ) {

        binding.playlistName.text = playlist.playlistName
        binding.playlistNameMenu.text = playlist.playlistName
        binding.playListCountMenu.text = TextFormatter.tracksCountFormat(tracksCount)

        Glide.with(binding.playlistCoverMenu)
            .load(playlist.playlistCoverPath)
            .placeholder(R.drawable.playlist_details_cover_placeholder)
            .centerCrop()
            .into(binding.playlistCoverMenu)

        if (playlist.playlistDescription.isNotEmpty()) {
            binding.playlistDescription.text = playlist.playlistDescription
            binding.playlistDescription.isVisible = true
        } else {
            binding.playlistDescription.isVisible = false
        }

        Glide.with(binding.playlistCover)
            .load(playlist.playlistCoverPath)
            .placeholder(R.drawable.playlist_details_cover_placeholder)
            .centerCrop()
            .into(binding.playlistCover)

        binding.playlistTime.text = TextFormatter.formatDurationMinutes(totalDuration)
        binding.playlistTracksCount.text = TextFormatter.tracksCountFormat(tracksCount)

        if (tracks.isNotEmpty()) {
            this.tracks.clear()
            this.tracks.addAll(tracks)
            tracksAdapter.notifyDataSetChanged()
        }
    }

    private fun startShareIntent(intent: Intent) {
        startActivity(Intent.createChooser(intent, getString(R.string.share_playlist_title)))
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        viewModel.getPlaylistDetails()
        binding.overlay.isVisible = false
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    companion object {
        private const val ARGS_PLAYLIST = "SELECTED_PLAYLIST"
        fun createArgs(playlistId: Long): Bundle = bundleOf(ARGS_PLAYLIST to playlistId)
    }
}