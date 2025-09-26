package com.example.playlistmaker.sharing.domain

import android.content.Context
import com.example.playlistmaker.R
import com.example.playlistmaker.sharing.data.EmailData

class SharingInteractorImpl(private val externalNavigator: ExternalNavigator, private val context: Context) : SharingInteractor {
    override fun shareApp() {
        externalNavigator.shareApp(getShareAppLink())
    }

    private fun getShareAppLink(): String {
        return context.getString(R.string.android_developer_url)
    }

    override fun openTerms() {
        externalNavigator.openLink(getTermsLink())
    }

    private fun getTermsLink(): String {
        return context.getString(R.string.practicum_offer_url)
    }

    override fun openSupport() {
        externalNavigator.openEmail(getSupportEmailData())
    }

    private fun getSupportEmailData(): EmailData {
        return EmailData(
            email = context.getString(R.string.my_email),
            subject = context.getString(R.string.email_subject),
            body = context.getString(R.string.email_text)
        )
    }
}