package com.example.bottomnavbardemo

import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.R
import android.app.*

import android.graphics.Color
import androidx.room.Room
import com.example.bottomnavbardemo.database.AppDatabase
import com.example.bottomnavbardemo.models.notification


class MyFirebaseMessagingService: FirebaseMessagingService() {

    private val TAG = "FireBaseMessagingService"
    var NOTIFICATION_CHANNEL_ID = "com.example.bottomnavbardemo"
    val NOTIFICATION_ID = 100

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    private val db by lazy { AppDatabase.getDatabase(this).notificationDao() }
    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: ${remoteMessage.from}")
        Log.d(TAG, "${remoteMessage.notification?.title}")


        val noti = remoteMessage.notification?.body?.let { body->
            remoteMessage.notification!!.title?.let { title ->
                remoteMessage.messageId?.let { id ->
                    notification(
                        id,
                        title,
                        body,
                        remoteMessage.sentTime.toString()
                    )
                }
            }
        }
        if (noti != null) {
            db.insertOne(noti)
        }

        if (remoteMessage.data.size > 0) {

            sendNotification(remoteMessage)
        } else {
            sendNotification(remoteMessage)
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    private fun handleNow() {
        Log.d(TAG, "Short lived task is done.")
    }

    private fun sendNotification(remoteMessage: RemoteMessage) {
        val NOTIFICATION_ID = 234
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val CHANNEL_ID: String
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CHANNEL_ID = "my_channel_01"
            val name: CharSequence = "my_channel"
            val Description = "This is my channel"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
            mChannel.description = Description
            mChannel.enableLights(true)
            mChannel.lightColor = Color.RED
            mChannel.enableVibration(true)
            mChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            mChannel.setShowBadge(false)
            notificationManager.createNotificationChannel(mChannel)
        }

        val builder: NotificationCompat.Builder = NotificationCompat.Builder(this, "my_channel_01")
            .setSmallIcon(R.mipmap.sym_def_app_icon)
            .setContentTitle(remoteMessage.notification?.title)
            .setContentText(remoteMessage.notification?.body)

        val resultIntent = Intent(this, MainActivity::class.java)
        val stackBuilder: TaskStackBuilder = TaskStackBuilder.create(this)
        stackBuilder.addParentStack(MainActivity::class.java)
        stackBuilder.addNextIntent(resultIntent)
        val resultPendingIntent: PendingIntent =
            stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        builder.setContentIntent(resultPendingIntent)
        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }





    companion object {

        private const val TAG = "MyFirebaseMsgService"
    }
}
