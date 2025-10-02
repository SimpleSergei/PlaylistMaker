package com.example.playlistmaker.sharing.domain

import android.content.Context
import com.example.playlistmaker.R
import com.example.playlistmaker.sharing.data.EmailData

class SharingInteractorImpl(private val externalNavigator: ExternalNavigator,private val context: Context) : SharingInteractor {
    override fun shareApp(context: Context) {
        externalNavigator.shareApp(getShareAppLink(),context)
    }

    private fun getShareAppLink(): String {
        return context.getString(R.string.android_developer_url)
    }

    override fun openTerms(context: Context) {
        externalNavigator.openLink(getTermsLink(),context)
    }

    private fun getTermsLink(): String {
        return context.getString(R.string.practicum_offer_url)
    }

    override fun openSupport(context: Context) {
        externalNavigator.openEmail(getSupportEmailData(),context)
    }

    private fun getSupportEmailData(): EmailData {
        return EmailData(
            email = context.getString(R.string.my_email),
            subject = context.getString(R.string.email_subject),
            body = context.getString(R.string.email_text)
        )
    }
}