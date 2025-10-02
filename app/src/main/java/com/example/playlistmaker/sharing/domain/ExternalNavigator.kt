package com.example.playlistmaker.sharing.domain

import android.content.Context
import android.content.Intent
import com.example.playlistmaker.sharing.data.EmailData
import androidx.core.net.toUri

class ExternalNavigator() {

    fun shareApp(link: String,context: Context) {
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, link)
        context.startActivity(Intent.createChooser(shareIntent, null))
    }

    fun openLink(link: String,context: Context) {
        val offerIntent = Intent(Intent.ACTION_VIEW, link.toUri())
        context.startActivity(offerIntent)
    }

    fun openEmail(emailData: EmailData,context: Context){
        val supportIntent = Intent()
        supportIntent.action = Intent.ACTION_SENDTO
        supportIntent.data = "mailto:".toUri()
        supportIntent.putExtra(Intent.EXTRA_EMAIL, emailData.email)
        supportIntent.putExtra(Intent.EXTRA_TEXT, emailData.body)
        supportIntent.putExtra(Intent.EXTRA_SUBJECT, emailData.subject)
        context.startActivity(supportIntent)
    }
}