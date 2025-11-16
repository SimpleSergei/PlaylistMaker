package com.example.playlistmaker.di

import android.content.Context
import androidx.room.Room
import com.example.playlistmaker.library.data.FavoriteRepositoryImpl
import com.example.playlistmaker.library.data.db.AppDataBase
import com.example.playlistmaker.library.data.db.TrackDbConverter
import com.example.playlistmaker.library.data.db.dao.TrackDao
import com.example.playlistmaker.library.domain.FavoriteRepository
import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.SearchHistoryRepositoryImpl
import com.example.playlistmaker.search.data.StorageClient
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.data.network.iTunesApi
import com.example.playlistmaker.search.data.storage.PrefsStorageClient
import com.example.playlistmaker.search.data.storage.SEARCH_HISTORY_KEY
import com.example.playlistmaker.search.domain.SearchHistoryRepository
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.settings.data.SettingsRepositoryImpl
import com.example.playlistmaker.settings.domain.SettingsRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {

    single<iTunesApi> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(iTunesApi::class.java)
    }
    single<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(get(), get())
    }

    single<StorageClient<ArrayList<Track>>> {
        PrefsStorageClient(
            androidContext(),
            SEARCH_HISTORY_KEY,
            object : TypeToken<ArrayList<Track>>() {}.type
        )
    }

    single {
        androidContext()
            .getSharedPreferences(SEARCH_HISTORY_KEY, Context.MODE_PRIVATE)
    }
    single<SettingsRepository> {
        SettingsRepositoryImpl(get())
    }
    single<NetworkClient> {
        RetrofitNetworkClient(get(),get())
    }
    factory { Gson() }

    single {
        Room.databaseBuilder(androidContext(), AppDataBase::class.java, "database.db")
            .build()
    }
    factory { TrackDbConverter() }
    single<FavoriteRepository> {
        FavoriteRepositoryImpl(get(),get())
    }
    single<TrackDao> { get<AppDataBase>().trackDao() }

}
