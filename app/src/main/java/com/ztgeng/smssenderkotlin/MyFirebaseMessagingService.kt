package com.ztgeng.smssenderkotlin

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String?) {
        Log.d("gengz", "On new token: $token")
        NetworkClient.getInstance(this.applicationContext).sendToken(this, token ?: return)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        Log.d("gengz", "From: ${remoteMessage?.from}")
        remoteMessage?.data?.isNotEmpty()?.let { Log.d("gengz", "Data: ${remoteMessage.data}") }
        remoteMessage?.notification?.let { Log.d("gengz", "Message: ${it.title} - ${it.body}") }
    }
}
