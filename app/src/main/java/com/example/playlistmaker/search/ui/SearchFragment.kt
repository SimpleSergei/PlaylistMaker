package com.example.playlistmaker.search.ui

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.player.ui.PlayerFragment
import com.example.playlistmaker.search.data.Track
import com.example.playlistmaker.search.domain.TracksSearchState
import com.example.playlistmaker.utils.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private var editTextValue: String = TEXT_DEFAULT
    private var userRequest: String = ""
    private val tracks = ArrayList<Track>()
    private val tracksHistory = ArrayList<Track>()

    lateinit var tracksAdapter: TrackAdapter
    private lateinit var searchHistoryAdapter: TrackAdapter
    private val viewModel: TracksSearchViewModel by viewModel<TracksSearchViewModel>()
    private lateinit var onTrackClickDebounce: (Track) -> Unit

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        onTrackClickDebounce = debounce(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { track ->
            findNavController().navigate(
                R.id.action_searchFragment_to_playerFragment,
                PlayerFragment.createArgs(track)
            )
        }

        searchHistoryAdapter = TrackAdapter(tracksHistory)
        binding.recyclerViewHistory.adapter = searchHistoryAdapter

        searchHistoryAdapter.onTrackClickListener = { trackForecast ->
            onTrackClickDebounce(trackForecast)
        }

        tracksAdapter = TrackAdapter(tracks)
        binding.recyclerView.adapter = tracksAdapter

        tracksAdapter.onTrackClickListener = { trackForecast ->
            onTrackClickDebounce(trackForecast)
                viewModel.saveToHistory(trackForecast)
            }

        binding.clearHistoryBtn.setOnClickListener {
            viewModel.clearSearchHistory()
            with(binding) {
                youSearchTxt.visibility = View.GONE
                recyclerViewHistory.visibility = View.GONE
                clearHistoryBtn.visibility = View.GONE
            }
        }

        binding.clearBtn.setOnClickListener {
            binding.inputEditText.setText("")
            tracks.clear()
            tracksAdapter.notifyDataSetChanged()
            with(binding) {
                progressBar.visibility = View.GONE
                errorMessage.visibility = View.GONE
                nothingFoundImg.visibility = View.GONE
                somethingWentWrongImg.visibility = View.GONE
                refreshBtn.visibility = View.GONE
                recyclerView.visibility = View.GONE
            }
            viewModel.loadSearchHistory()
            val inputMethodManager =
                requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(binding.clearBtn.windowToken, 0)
        }

        binding.refreshBtn.setOnClickListener {
            viewModel.searchDebounce(userRequest, true)
            binding.errorMessage.visibility = View.GONE
            binding.somethingWentWrongImg.visibility = View.GONE
            binding.refreshBtn.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE
        }

        binding.inputEditText.doOnTextChanged { text, _, _, _ ->
            binding.clearBtn.visibility = clearButtonVisibility(text?.toString())
            editTextValue = text?.toString() ?: ""
            userRequest = text?.toString() ?: ""
            if (!text.isNullOrEmpty()) {
                viewModel.searchDebounce(changedText = text.toString())
            }
        }

        binding.inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (binding.inputEditText.text.isNotEmpty()) {
                    userRequest = binding.inputEditText.text.toString()
                    viewModel.searchDebounce(changedText = userRequest)
                }
            }
            false
        }
    }

    private fun clearButtonVisibility(s: String?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    fun render(state: TracksSearchState) {
        hideAllStates()
        when (state) {
            is TracksSearchState.Loading -> showLoading()
            is TracksSearchState.Content -> showContent(state.track)
            is TracksSearchState.Empty -> showEmpty()
            is TracksSearchState.Error -> showError()
            is TracksSearchState.SearchHistory -> showSearchHistory(state.history)
            is TracksSearchState.SearchHistoryCleared -> hideSearchHistory()
        }
    }

    private fun showSearchHistory(history: List<Track>) {
        tracksHistory.clear()
        if (history.isNotEmpty()) {
            tracksHistory.addAll(history)
            searchHistoryAdapter.notifyDataSetChanged()
            with(binding) {
                youSearchTxt.visibility = View.VISIBLE
                recyclerViewHistory.visibility = View.VISIBLE
                clearHistoryBtn.visibility = View.VISIBLE
            }
        } else {
            hideSearchHistory()
        }
    }

    private fun hideSearchHistory() {
        with(binding) {
            youSearchTxt.visibility = View.GONE
            recyclerViewHistory.visibility = View.GONE
            clearHistoryBtn.visibility = View.GONE
        }
    }

    private fun showError() {
        binding.errorMessage.visibility = View.VISIBLE
        tracks.clear()
        tracksAdapter.notifyDataSetChanged()
        binding.errorMessage.text = getString(R.string.something_went_wrong)
        binding.somethingWentWrongImg.visibility = View.VISIBLE
        binding.refreshBtn.visibility = View.VISIBLE
    }

    private fun showEmpty() {
        binding.errorMessage.visibility = View.VISIBLE
        tracks.clear()
        tracksAdapter.notifyDataSetChanged()
        binding.errorMessage.text = getString(R.string.nothing_found)
        binding.nothingFoundImg.visibility = View.VISIBLE
    }

    private fun showContent(tracksList: List<Track>) {
        binding.recyclerView.visibility = View.VISIBLE
        tracks.clear()
        tracks.addAll(tracksList)
        tracksAdapter.notifyDataSetChanged()
    }

    private fun showLoading() {
           binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideAllStates() {
        with(binding) {
            progressBar.visibility = View.GONE
            errorMessage.visibility = View.GONE
            nothingFoundImg.visibility = View.GONE
            somethingWentWrongImg.visibility = View.GONE
            refreshBtn.visibility = View.GONE
            recyclerView.visibility = View.GONE
            youSearchTxt.visibility = View.GONE
            recyclerViewHistory.visibility = View.GONE
            clearHistoryBtn.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TEXT_DEFAULT = ""
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}
