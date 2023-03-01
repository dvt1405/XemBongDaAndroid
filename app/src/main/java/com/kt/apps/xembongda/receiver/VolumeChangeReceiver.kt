package com.kt.apps.xembongda.receiver

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.media.session.MediaButtonReceiver

class VolumeChangeReceiver : MediaButtonReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)

    }

    companion object {
        private var instance: VolumeChangeReceiver? = null
        fun getInstance() = instance ?: VolumeChangeReceiver().also {
            instance = it
        }
    }
}