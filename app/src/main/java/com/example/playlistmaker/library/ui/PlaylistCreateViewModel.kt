package com.example.playlistmaker.library.ui

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.domain.PlaylistInteractor
import kotlinx.coroutines.launch

class PlaylistCreateViewModel(private val playlistInteractor: PlaylistInteractor) :
    ViewModel() {

    private val isButtonEnabledLiveData = MutableLiveData<Boolean>(false)
    fun observeIsButtonEnabled(): LiveData<Boolean> = isButtonEnabledLiveData

    fun updateCreateButtonState(playlistName: String) {
        isButtonEnabledLiveData.postValue(playlistName.isNotEmpty())
    }

    fun createPlaylist(playlistName:String, playlistDescription: String, context: Context, uri: Uri?) {
        viewModelScope.launch {
            val coverPath = if (uri != null) {
                val internalUri = playlistInteractor.copyImageToInternalStorage(context, uri)
                internalUri.toString()
            } else {""}
            playlistInteractor.createPlaylist(playlistName,playlistDescription,coverPath)
        }
    }
}
