package com.example.playlistmaker.sharing.domain

import android.content.Intent

interface SharingInteractor {
    fun getShareAppIntent(): Intent
    fun getSupportIntent(): Intent
    fun getTermsIntent():Intent
}
