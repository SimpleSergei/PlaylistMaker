package com.example.playlistmaker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Callback
import retrofit2.Response

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
    private val searchRunnable = Runnable { search(userRequest) }
    private val handler = Handler(Looper.getMainLooper())
    lateinit var tracksAdapter: TrackAdapter
    private lateinit var searchHistoryAdapter: TrackAdapter

    private val iTunesBaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val iTunesService = retrofit.create(iTunesApi::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        _binding = ActivitySearchBinding.inflate(layoutInflater)
        val sharedPrefs = getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)
        val searchHistory = SearchHistory(sharedPrefs)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.search)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        searchHistoryAdapter = TrackAdapter(tracksHistory)
        binding.recyclerViewHistory.adapter = searchHistoryAdapter

        if (searchHistory.getSearchHistory() != null) {
            tracksHistory.addAll(searchHistory.getSearchHistory()!!.reversed())
            showSearchHistory()
        }

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
                searchHistory.addTrackToHistory(trackForecast)
                playerIntent.putExtra("selected_track", Gson().toJson(trackForecast))
                startActivity(playerIntent)
            }
        }

        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.clearHistoryBtn.setOnClickListener {
            searchHistory.clearTrackHistory()
            hideSearchHistory()
        }

        binding.clearBtn.setOnClickListener {
            binding.inputEditText.setText("")
            tracks.clear()
            tracksAdapter.notifyDataSetChanged()
            with(binding) {
                errorMessage.visibility = View.GONE
                nothingFoundImg.visibility = View.GONE
                somethingWentWrongImg.visibility = View.GONE
                refreshBtn.visibility = View.GONE
                recyclerView.visibility = View.GONE
            }
            if (searchHistory.getSearchHistory() != null) {
                tracksHistory.clear()
                tracksHistory.addAll(searchHistory.getSearchHistory()!!.reversed())
                showSearchHistory()
            }
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(binding.clearBtn.windowToken, 0)
        }

        binding.refreshBtn.setOnClickListener {
            search(userRequest)
        }

        binding.inputEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.inputEditText.text.isEmpty() && searchHistory.getSearchHistory() != null) {
                showSearchHistory()
            } else hideSearchHistory()
        }
        binding.inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (binding.inputEditText.text.isNotEmpty()) {
                    userRequest = binding.inputEditText.text.toString()
                    search(userRequest)
                }
            }
            false
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.clearBtn.visibility = clearButtonVisibility(s.toString())
                editTextValue = s.toString()
                userRequest = s.toString()
                if (s?.isNotEmpty() == true) searchDebounce()
                if (binding.inputEditText.hasFocus() && s?.isEmpty() == true) {
                    with(binding) {
                        recyclerView.visibility = View.GONE
                        errorMessage.visibility = View.GONE
                        nothingFoundImg.visibility = View.GONE
                        somethingWentWrongImg.visibility = View.GONE
                        refreshBtn.visibility = View.GONE
                        recyclerView.visibility = View.GONE
                    }
                }
                if (binding.inputEditText.hasFocus() && s?.isEmpty() == true && searchHistory.getSearchHistory() != null) {
                    tracksHistory.clear()
                    tracksHistory.addAll(searchHistory.getSearchHistory()!!.reversed())
                    showSearchHistory()
                } else hideSearchHistory()
            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }
        binding.inputEditText.addTextChangedListener(simpleTextWatcher)
    }

    private fun search(request: String) {
        binding.recyclerView.visibility = View.GONE
        hideSearchHistory()
        binding.progressBar.visibility = View.VISIBLE

        iTunesService.findSong(request).enqueue(object : Callback<TracksResponse> {
            override fun onResponse(
                call: Call<TracksResponse>,
                response: Response<TracksResponse>
            ) {
                binding.progressBar.visibility = View.GONE
                if (response.code() == 200) {
                    tracks.clear()
                    if (response.body()?.results?.isNotEmpty() == true) {
                        tracks.addAll(response.body()?.results!!)
                        tracksAdapter.notifyDataSetChanged()
                        binding.recyclerView.visibility = View.VISIBLE
                    }
                    if (tracks.isEmpty()) {
                        showErrorMessage(getString(R.string.nothing_found), 1)
                    } else {
                        showErrorMessage("", 2)
                    }
                } else {
                    showErrorMessage(getString(R.string.something_went_wrong), 2)
                }
            }

            override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                showErrorMessage(getString(R.string.something_went_wrong), 2)
            }
        })
    }

    private fun showErrorMessage(text: String, type: Int) {
        if (text.isNotEmpty()) {
            binding.errorMessage.visibility = View.VISIBLE
            tracks.clear()
            tracksAdapter.notifyDataSetChanged()
            binding.errorMessage.text = text
            when (type) {
                1 -> binding.nothingFoundImg.visibility = View.VISIBLE
                2 -> {
                    binding.somethingWentWrongImg.visibility = View.VISIBLE
                    binding.refreshBtn.visibility = View.VISIBLE
                }
            }
        } else {
            with(binding) {
                errorMessage.visibility = View.GONE
                nothingFoundImg.visibility = View.GONE
                somethingWentWrongImg.visibility = View.GONE
                refreshBtn.visibility = View.GONE
            }
        }
    }

    private fun clearButtonVisibility(s: String?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(TEXT_AMOUNT, editTextValue)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        editTextValue = savedInstanceState.getString(TEXT_AMOUNT, TEXT_DEFAULT)
        val inputEditText = findViewById<EditText>(R.id.inputEditText)
        inputEditText.setText(editTextValue)
    }

    private fun showSearchHistory() {
        with(binding) {
            searchHistoryAdapter.notifyDataSetChanged()
            youSearchTxt.visibility = View.VISIBLE
            recyclerViewHistory.visibility = View.VISIBLE
            clearHistoryBtn.visibility = View.VISIBLE
        }
    }

    private fun hideSearchHistory() {
        with(binding) {
            youSearchTxt.visibility = View.GONE
            recyclerViewHistory.visibility = View.GONE
            clearHistoryBtn.visibility = View.GONE
        }
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }


    companion object {
        private const val TEXT_AMOUNT = "TEXT_AMOUNT"
        private const val TEXT_DEFAULT = ""
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}