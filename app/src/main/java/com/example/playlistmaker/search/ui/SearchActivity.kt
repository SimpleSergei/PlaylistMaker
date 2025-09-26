package com.example.playlistmaker.search.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.player.ui.PlayerActivity
import com.example.playlistmaker.search.data.Track
import com.example.playlistmaker.search.domain.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.TracksSearchState
import com.google.gson.Gson

class SearchActivity : AppCompatActivity() {
    private var _binding: ActivitySearchBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding for search activity must not be null")

    private var isClickAllowed = true
    private var editTextValue: String = TEXT_DEFAULT
    private var userRequest: String = ""
    private val tracks = ArrayList<Track>()
    private val tracksHistory = ArrayList<Track>()

    private val handler = Handler(Looper.getMainLooper())
    lateinit var tracksAdapter: TrackAdapter
    private lateinit var searchHistoryAdapter: TrackAdapter
    private var viewModel: TracksSearchViewModel? = null
    private var simpleTextWatcher: TextWatcher? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        _binding = ActivitySearchBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.search)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        viewModel = ViewModelProvider(this, TracksSearchViewModel.getFactory()).get(
            TracksSearchViewModel::class.java
        )
        viewModel?.observeState()?.observe(this) {
            render(it)
        }
        val searchHistoryInteractor = Creator.provideSearchHistoryInteractor(this)
        searchHistoryAdapter = TrackAdapter(tracksHistory)
        binding.recyclerViewHistory.adapter = searchHistoryAdapter

        searchHistoryInteractor.getHistory(object : SearchHistoryInteractor.HistoryConsumer {
            override fun consume(searchHistory: List<Track>?) {
                if (searchHistory != null && searchHistory.isNotEmpty()) {
                    tracksHistory.addAll(searchHistory.reversed())
                    showSearchHistory()
                }
            }
        })

        searchHistoryAdapter.onTrackClickListener = { trackForecast ->
            if (clickDebounce()) {
                val playerIntent = Intent(this, PlayerActivity::class.java)
                playerIntent.putExtra("selected_track", Gson().toJson(trackForecast))
                startActivity(playerIntent)
            }
        }

        tracksAdapter = TrackAdapter(tracks)
        binding.recyclerView.adapter = tracksAdapter

        tracksAdapter.onTrackClickListener = { trackForecast ->
            if (clickDebounce()) {
                val playerIntent = Intent(this, PlayerActivity::class.java)
                searchHistoryInteractor.saveToHistory(trackForecast)
                playerIntent.putExtra("selected_track", Gson().toJson(trackForecast))
                startActivity(playerIntent)
            }
        }

        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.clearHistoryBtn.setOnClickListener {
            searchHistoryInteractor.clearHistory()
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
            searchHistoryInteractor.getHistory(object :
                SearchHistoryInteractor.HistoryConsumer {
                override fun consume(searchHistory: List<Track>?) {
                    if (searchHistory != null && searchHistory.isNotEmpty()) {
                        tracksHistory.clear()
                        tracksHistory.addAll(searchHistory.reversed())
                        showSearchHistory()
                    }
                }
            })
            val inputMethodManager =
                getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(binding.clearBtn.windowToken, 0)
        }

        binding.refreshBtn.setOnClickListener {
            viewModel?.searchDebounce(userRequest)
        }

        simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.clearBtn.visibility = clearButtonVisibility(s.toString())
                editTextValue = s.toString()
                userRequest = s.toString()
                if (s?.isNotEmpty() == true) viewModel?.searchDebounce(
                    changedText = s?.toString() ?: ""
                )
            }
        }
        simpleTextWatcher?.let { binding.inputEditText.addTextChangedListener(it) }
        binding.inputEditText.addTextChangedListener(simpleTextWatcher)

        binding.inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (binding.inputEditText.text.isNotEmpty()) {
                    userRequest = binding.inputEditText.text.toString()
                    viewModel?.searchDebounce(changedText = userRequest)
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

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    fun render(state: TracksSearchState) {
        when (state) {
            is TracksSearchState.Loading -> showLoading()
            is TracksSearchState.Content -> showContent(state.track)
            is TracksSearchState.Empty -> showEmpty()
            is TracksSearchState.Error -> showError()
        }
    }

    private fun showSearchHistory() {
        with(binding) {
            searchHistoryAdapter.notifyDataSetChanged()
            youSearchTxt.visibility = View.VISIBLE
            recyclerViewHistory.visibility = View.VISIBLE
            clearHistoryBtn.visibility = View.VISIBLE
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
        binding.progressBar.visibility = View.GONE
        tracks.clear()
        tracks.addAll(tracksList)
        tracksAdapter.notifyDataSetChanged()
    }

    private fun showLoading() {
        binding.recyclerView.visibility = View.GONE
        with(binding) {
            youSearchTxt.visibility = View.GONE
            recyclerViewHistory.visibility = View.GONE
            clearHistoryBtn.visibility = View.GONE
        }
        binding.progressBar.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        simpleTextWatcher?.let { binding.inputEditText.removeTextChangedListener(it) }
    }

    companion object {
        private const val TEXT_DEFAULT = ""
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}