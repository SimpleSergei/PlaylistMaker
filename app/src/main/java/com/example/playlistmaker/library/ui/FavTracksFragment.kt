package com.example.playlistmaker.library.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentFavTracksBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavTracksFragment : Fragment() {

    companion object {
        fun newInstance(): FavTracksFragment {
            return FavTracksFragment()
        }
    }
    private val viewModel: FavTracksViewModel by viewModel()

    private var _binding: FragmentFavTracksBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding for favorite tracks fragment must not be null")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavTracksBinding.inflate(inflater, container, false)
        return binding.root
    }
}