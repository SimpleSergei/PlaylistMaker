package com.example.playlistmaker.playlists.ui

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.core.os.bundleOf
import com.bumptech.glide.Glide
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlaylistEditFragment : PlaylistCreateFragment() {

    override val viewModel: PlaylistEditViewModel by viewModel<PlaylistEditViewModel> {
        parametersOf(
            playlistId
        )
    }
    private val playlistId: Long by lazy {
        requireArguments().getLong(ARGS_PLAYLIST_ID)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.headerTitle.text = "Редактировать"
        binding.playlistCreateBtn.text = "Сохранить"

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            parentFragmentManager.popBackStack()
        }
        binding.backBtn.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        viewModel.playlistData.observe(viewLifecycleOwner) { data ->
            data?.let {
                binding.playlistName.setText(it.name)
                binding.playlistDescription.setText(it.description)

                it.imageUri?.let { uri ->
                    Glide.with(binding.imageRectangle)
                        .load(uri)
                        .into(binding.imageRectangle)
                }
            }
        }
        viewModel.loadPlaylist()

    }

    override fun showSuccessToast(playlistName: String) {
        parentFragmentManager.popBackStack()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARGS_PLAYLIST_ID = "playlist_id"
        fun createArgs(playlistId: Long): Bundle = bundleOf(ARGS_PLAYLIST_ID to playlistId)
    }
}