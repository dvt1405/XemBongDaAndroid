package com.kt.apps.xembongda.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.media.RingtoneManager
import android.os.Build
import android.os.Build.VERSION
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.IconCompat
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.gson.Gson
import com.kt.apps.xembongda.MainActivity
import com.kt.apps.xembongda.R

class FcmService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.e("TAG", "onNewToken: $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        showNotification(message)
        Log.e("TAG", Gson().toJson(message))
        message.messageType?.let { Log.d(this::class.java.simpleName, it) }
        if (message.messageType == "RefreshRemoteConfig") {
            Firebase.remoteConfig.fetch()
                .addOnSuccessListener {

                }
                .addOnFailureListener {

                }
        } else if (message.messageType == "ShowNotification") {
            showNotification(message)
        }
    }

    @SuppressLint("WrongConstant")
    private fun showNotification(message: RemoteMessage) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = if (VERSION.SDK_INT >= 31) {
            PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_ONE_SHOT
            )
        } else {
            PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT
            )
        }
        val channelId = getString(R.string.firebase_notification_channel_id_default)
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(com.kt.apps.xembongda.base.R.drawable.app_icon)
            .setContentTitle(message.notification?.title)
            .setContentText(message.notification?.body)
            .setAutoCancel(true)
            .setSound(soundUri)
            .setContentIntent(pendingIntent)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(channelId, getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(0, notificationBuilder.build())
    }
}