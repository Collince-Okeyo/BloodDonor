package com.ramgdev.blooddonor.util

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ramgdev.blooddonor.R
import kotlin.random.Random

private const val CHANNEL_ID = "myChannel"

class NotificationService : FirebaseMessagingService() {

    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        val tokenMap = HashMap<String, String>()
        tokenMap["token"] = newToken

        FirebaseDatabase.getInstance().getReference("device_token").child(newToken)
            .setValue(tokenMap)
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val title = message.notification?.title
        val body = message.notification?.body

        val notificationManager = getSystemService((Context.NOTIFICATION_SERVICE)) as NotificationManager
        val notificationID = Random.nextInt()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.ic_baseline_notifications_24)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(notificationID, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channelName = "channelName"
        val channel = NotificationChannel(CHANNEL_ID, channelName, IMPORTANCE_HIGH).apply {
            description = "My channel description"
            enableLights(true)
            lightColor = Color.GREEN
        }

        notificationManager.createNotificationChannel(channel)
    }
}