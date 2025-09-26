package com.example.playlistmaker.search.data.storage

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.search.data.StorageClient
import com.google.gson.Gson
import java.lang.reflect.Type
import androidx.core.content.edit

class PrefsStorageClient<T>(
    context: Context, private val dataKey: String, private val type: Type
) : StorageClient<T> {
    companion object {
        private const val MAX_COUNT_TRACKS = 10
    }

    private val prefs: SharedPreferences =
        context.getSharedPreferences("SEARCH_HISTORY", Context.MODE_PRIVATE)
    private val gson = Gson()
    override fun storageData(data: T) {
        require(data is ArrayList<*>) { "Data must be ArrayList" }
        val dataList = data as ArrayList<*>
        if (dataList.size > MAX_COUNT_TRACKS) dataList.removeAt(0)
        prefs.edit { putString(dataKey, gson.toJson(dataList, type)) }
    }

    override fun getData(): T? {
        val dataJson = prefs.getString(dataKey, null)
        return if (dataJson == null) {
            null
        } else {
            gson.fromJson(dataJson, type)
        }
    }

    override fun deleteData() {
        prefs.edit {
            remove("SEARCH_HISTORY")
        }
    }
}