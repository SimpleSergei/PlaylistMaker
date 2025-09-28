package com.example.playlistmaker.sharing.domain

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.playlistmaker.sharing.data.EmailData

class ExternalNavigator(private val context: Context) {

    fun shareApp(link: String) {
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, link)
        context.startActivity(Intent.createChooser(shareIntent, null))
    }

    fun openLink(link: String) {
        val offerIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        context.startActivity(offerIntent)
    }

    fun openEmail(emailData: EmailData){
        val supportIntent = Intent()
        supportIntent.action = Intent.ACTION_SENDTO
        supportIntent.data = Uri.parse("mailto:")
        supportIntent.putExtra(Intent.EXTRA_EMAIL, emailData.email)
        supportIntent.putExtra(Intent.EXTRA_TEXT, emailData.body)
        supportIntent.putExtra(Intent.EXTRA_SUBJECT, emailData.subject)
        context.startActivity(supportIntent)
    }
}