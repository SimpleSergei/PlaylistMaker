package com.example.playlistmaker.sharing.domain

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.example.playlistmaker.R
import com.example.playlistmaker.sharing.data.EmailData

class SharingInteractorImpl(private val context: Context) : SharingInteractor {

    override fun getShareAppIntent(): Intent {
        val link = context.getString(R.string.android_developer_url)
        return Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, link)
        }
    }

    override fun getTermsIntent(): Intent {
        val link = context.getString(R.string.practicum_offer_url)
        return Intent(Intent.ACTION_VIEW, link.toUri())
    }

    override fun getSupportIntent(): Intent {
        val emailData = EmailData(
            email = context.getString(R.string.my_email),
            subject = context.getString(R.string.email_subject),
            body = context.getString(R.string.email_text)
        )
        return Intent(Intent.ACTION_SENDTO).apply {
            data = "mailto:${emailData.email}".toUri()
            putExtra(Intent.EXTRA_SUBJECT, emailData.subject)
            putExtra(Intent.EXTRA_TEXT, emailData.body)
        }
    }
}