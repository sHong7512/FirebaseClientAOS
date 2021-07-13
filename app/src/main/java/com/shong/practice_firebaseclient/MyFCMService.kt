package com.shong.practice_firebaseclient

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFCMService : FirebaseMessagingService() {
    private val TAG = this::class.java.simpleName + "_sHong"
    private val NOTIFICATION_ID = 7512

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d(TAG + "_onMessageReceived", "From: ${remoteMessage.from}")
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG + "_onMessageReceived", "CollapseKey: ${remoteMessage.collapseKey}")
            Log.d(TAG + "_onMessageReceived", "Data: ${remoteMessage.data}")
            Log.d(TAG + "_onMessageReceived", "Notification: ${remoteMessage.notification}")
            Log.d(TAG + "_onMessageReceived", "SentTime(Millis): ${remoteMessage.sentTime}")
        }

        remoteMessage.notification?.let {
            Log.d(TAG + "_onMessageReceived", "Message Notification Body: ${it.body}")
            it.title?.let { it1 -> it.body?.let { it2 -> sendNotification(it1, it2) } }
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        Log.d(TAG + "_onNewToken", "Refreshed token: $token")
        setStoreToken(token)
    }

    private fun sendNotification(title: String, messageBody: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK //or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            NOTIFICATION_ID , //requestCode
            intent,
            PendingIntent.FLAG_ONE_SHOT
//            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val channelId = "sHong_FCM"
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
            .setDefaults(NotificationCompat.DEFAULT_ALL)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            channelId,
            "FCM 메세지",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)

        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    fun setStoreToken(token: String){
        val pref = applicationContext.getSharedPreferences("test", Context.MODE_PRIVATE)
        pref.edit().putString("FCMToken", token).apply()
    }
}