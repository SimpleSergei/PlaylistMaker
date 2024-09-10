package com.example.playlistmaker

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : AppCompatActivity() {

    private var editTextValue: String = TEXT_DEFAULT
    private var userRequest:String =""
    private val tracks = ArrayList<Track>()
    private val tracksAdapter = TrackAdapter(tracks)

    private lateinit var errorText: TextView
    private lateinit var errorImageNotFound: ImageView
    private lateinit var errorImageWentWrong: ImageView
    private lateinit var refreshButton: Button

    private val iTunesBaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val iTunesService = retrofit.create(iTunesApi::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.search)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val backButton = findViewById<ImageView>(R.id.back)
        val clearButton = findViewById<ImageView>(R.id.clearButton)
        val inputEditText = findViewById<EditText>(R.id.inputEditText)
        errorText = findViewById(R.id.errorMessage)
        errorImageNotFound = findViewById(R.id.nothing_found)
        errorImageWentWrong = findViewById(R.id.something_went_wrong)
        refreshButton = findViewById(R.id.refreshButton)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        recyclerView.adapter = tracksAdapter

        backButton.setOnClickListener {
            finish()
        }

        clearButton.setOnClickListener {
            inputEditText.setText("")
            tracks.clear()
            tracksAdapter.notifyDataSetChanged()
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(clearButton.windowToken, 0)
        }

        refreshButton.setOnClickListener {
            search(userRequest)

        }

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (inputEditText.text.isNotEmpty()) {
                    userRequest = inputEditText.text.toString()
                    search(userRequest)
                }
                true
            }
            false
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = clearButtonVisibility(s.toString())
                editTextValue = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }
        inputEditText.addTextChangedListener(simpleTextWatcher)
    }

    private fun search(request: String) {
        iTunesService.findSong(request).enqueue(object : Callback<TracksResponse> {
            override fun onResponse(
                call: Call<TracksResponse>,
                response: Response<TracksResponse>
            ) {
                if (response.code() == 200) {
                    tracks.clear()
                    if (response.body()?.results?.isNotEmpty() == true) {
                        tracks.addAll(response.body()?.results!!)
                        tracksAdapter.notifyDataSetChanged()
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
                showErrorMessage(getString(R.string.something_went_wrong), 2)
            }
        })
    }

    private fun showErrorMessage(text: String, type: Int) {
        if (text.isNotEmpty()) {
            errorText.visibility = View.VISIBLE
            tracks.clear()
            tracksAdapter.notifyDataSetChanged()
            errorText.text = text
            when (type) {
                1 -> errorImageNotFound.visibility = View.VISIBLE
                2 -> {
                    errorImageWentWrong.visibility = View.VISIBLE
                    refreshButton.visibility = View.VISIBLE
                }
            }
        } else {
            errorText.visibility = View.GONE
            errorImageNotFound.visibility = View.GONE
            errorImageWentWrong.visibility = View.GONE
            refreshButton.visibility = View.GONE
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

    companion object {
        const val TEXT_AMOUNT = "TEXT_AMOUNT"
        const val TEXT_DEFAULT = ""
    }
}
