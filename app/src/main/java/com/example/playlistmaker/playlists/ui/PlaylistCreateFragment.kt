package com.example.playlistmaker.playlists.ui

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistCreationBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

open class PlaylistCreateFragment : Fragment() {
    var _binding: FragmentPlaylistCreationBinding? = null
    val binding get() = _binding!!
    open val viewModel: PlaylistCreateViewModel by viewModel<PlaylistCreateViewModel>()
    var hasImage = false
    var selectedImageUri: Uri? = null
    val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                hasImage = true
                selectedImageUri = uri
                Glide.with(binding.imageRectangle)
                    .load(uri)
                    .into(binding.imageRectangle)
            }
        }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlaylistCreationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (checkForUnsavedChanges()) {
                showExitConfirmationDialog()
            } else {
                parentFragmentManager.popBackStack()
            }

        }

        binding.playlistName.doOnTextChanged { text, _, _, _ ->
            viewModel.updateCreateButtonState(text?.toString() ?: "")
        }

        binding.backBtn.setOnClickListener {
            if (checkForUnsavedChanges()) {
                showExitConfirmationDialog()
            } else {
                parentFragmentManager.popBackStack()
            }
        }

        viewModel.observeIsButtonEnabled().observe(viewLifecycleOwner) {
            binding.playlistCreateBtn.isEnabled = it
        }

        binding.playlistCreateBtn.setOnClickListener {
            val playlistName = binding.playlistName.text?.toString() ?: ""
            val playlistDescription = binding.playlistDescription.text?.toString() ?: ""
            viewModel.createPlaylist(playlistName,playlistDescription,requireContext(),selectedImageUri)
                    showSuccessToast(playlistName)
        }

        binding.imageRectangle.setOnClickListener {
            pickMedia.launch(
                PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.ImageOnly
                )
            )
        }
    }

    open fun showSuccessToast(playlistName:String){
        Toast.makeText(
            requireContext(),
            getString(R.string.playlist_create, playlistName),
            Toast.LENGTH_LONG
        ).show()
        parentFragmentManager.popBackStack()
    }

    private fun showExitConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.finish_playlist_creation))
            .setMessage(getString(R.string.unsaved_data_warning))
            .setPositiveButton(getString(R.string.finish)) { dialog, _ ->
                dialog.dismiss()
                parentFragmentManager.popBackStack()
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun checkForUnsavedChanges(): Boolean {
        val hasName = !binding.playlistName.text.isNullOrEmpty()
        val hasDescription = !binding.playlistDescription.text.isNullOrEmpty()
        return hasImage || hasName || hasDescription
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
