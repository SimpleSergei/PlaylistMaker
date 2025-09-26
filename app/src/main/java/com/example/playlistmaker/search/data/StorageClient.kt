package com.example.playlistmaker.search.data

interface StorageClient<T> {
    fun storageData(data:T)
    fun getData():T?
    fun deleteData()
}