package com.example.playlistmaker.library.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.domain.Playlist
import com.example.playlistmaker.library.domain.PlaylistInteractor
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class PlaylistCreateViewModel(private val playlistInteractor: PlaylistInteractor) :
    ViewModel() {

    private val isButtonEnabledLiveData = MutableLiveData<Boolean>(false)
    fun observeIsButtonEnabled(): LiveData<Boolean> = isButtonEnabledLiveData

    fun updateCreateButtonState(playlistName: String) {
        isButtonEnabledLiveData.postValue(playlistName.isNotEmpty())
    }

    fun createPlaylist(playlist: Playlist) {
        viewModelScope.launch {
            playlistInteractor.createPlaylist(playlist)
        }
    }

    fun copyImageToInternalStorage(context: Context, uri: Uri, onComplete: (Uri) -> Unit) {
        val contentResolver = context.contentResolver
        val filePath = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "PlaylistCoverAlbum")
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        val timeStamp = System.currentTimeMillis()
        val file = File(filePath, "playlist_cover_$timeStamp")
        val inputStream = contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
        onComplete(Uri.fromFile(file))
    }
}
