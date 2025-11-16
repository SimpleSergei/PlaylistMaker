package com.example.playlistmaker.player.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.player.domain.PlayerState
import com.example.playlistmaker.search.domain.Track
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerFragment : Fragment() {
    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlayerViewModel by viewModel<PlayerViewModel> { parametersOf(track) }
    private lateinit var track: Track

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        track = Gson().fromJson(requireArguments().getString(ARGS_TRACK), Track::class.java)

        if (track.isFavorite) {
            binding.favoriteBtn.setImageResource(R.drawable.favorite_true_button)
        }
        else {
            binding.favoriteBtn.setImageResource(R.drawable.favorite_false_button)
        }

        viewModel.playerState.observe(viewLifecycleOwner) { state ->
            when (state) {
                PlayerState.Default -> {
                    binding.playBtn.setImageResource(R.drawable.play_button)
                    binding.trackTime.text = "00:00"
                }

                PlayerState.Prepared -> {
                    binding.playBtn.setImageResource(R.drawable.play_button)
                    binding.trackTime.text = "00:00"
                }

                is PlayerState.Playing -> {
                    binding.playBtn.setImageResource(R.drawable.pause_button)
                    binding.trackTime.text = state.progressTime
                }

                is PlayerState.Paused -> {
                    binding.playBtn.setImageResource(R.drawable.play_button)
                    binding.trackTime.text = state.progressTime
                }
            }
        }

        viewModel.isFavorite.observe(viewLifecycleOwner) { isFavorite ->
            when (isFavorite) {
                true -> binding.favoriteBtn.setImageResource(R.drawable.favorite_true_button)
                false -> binding.favoriteBtn.setImageResource(R.drawable.favorite_false_button)
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            parentFragmentManager.popBackStack()
        }
        binding.backBtn.setOnClickListener { parentFragmentManager.popBackStack() }
        binding.playBtn.setOnClickListener {
            viewModel.onPlayButtonClicked()
        }
        binding.favoriteBtn.setOnClickListener { viewModel.onFavoriteClicked() }


        Glide.with(binding.coverTrack)
            .load(track.artworkUrl100.replaceAfterLast("/", "512x512bb.jpg"))
            .placeholder(R.drawable.cover_mockup)
            .centerCrop()
            .transform(RoundedCorners(10))
            .into(binding.coverTrack)

        binding.groupAlbum.isVisible = !track.collectionName.isEmpty()

        with(binding) {
            trackName.text = track.trackName
            artistName.text = track.artistName
            trackDuration.text =
                SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
            trackAlbum.text = track.collectionName
            trackYear.text = track.releaseDate.substring(0, 4)
            trackGenre.text = track.primaryGenreName
            trackCountry.text = track.country
        }

    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARGS_TRACK = "SELECTED_TRACK"
        fun createArgs(track: Track): Bundle = bundleOf(ARGS_TRACK to Gson().toJson(track))
    }
}
