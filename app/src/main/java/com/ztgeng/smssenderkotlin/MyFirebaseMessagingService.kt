package com.ztgeng.smssenderkotlin

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

private const val FETCH_SMS_KEY = "fetch_sms"
private const val SMS_NUMBER_KEY = "number"
private const val SERVER_IP_KEY = "server"
private const val DEFAULT_SMS_NUMBER = 5

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String?) {
        Log.d("gengz", "On new token: $token")
        if (!token.isNullOrBlank()) {
            NetworkClient.getInstance(this.applicationContext).sendToken(getServerName(this), token)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        Log.d("gengz", "From: ${remoteMessage?.from}")
        remoteMessage?.data?.isNotEmpty()?.let { Log.d("gengz", "Data: ${remoteMessage.data}") }
        remoteMessage?.notification?.let { Log.d("gengz", "Message: ${it.title} - ${it.body}") }

        when (remoteMessage?.collapseKey) {
            FETCH_SMS_KEY -> {
                val url = remoteMessage.data?.get(SERVER_IP_KEY)
                NetworkClient.getInstance(this.applicationContext).sendSms(
                        url ?: getServerName(this),
                        readSms(remoteMessage.data?.get(SMS_NUMBER_KEY)?.toInt() ?: DEFAULT_SMS_NUMBER))
                url?.let { saveServerName(this, it) }
            }
        }
    }

    private fun readSms(number: Int): JSONArray {
        val msgs = JSONArray()
        val cursor = contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, null) ?: return msgs
        if (cursor.moveToFirst()) {
            do {
                val msg = JSONObject()
                for (i in 0 until cursor.columnCount) {
                    when (cursor.getColumnName(i)) {
                        "address" -> msg.put("number", cursor.getString(i))
                        "date" -> msg.put("time", Date(cursor.getLong(i)).toString())
                        "body" -> msg.put("message", cursor.getString(i))
                    }
                }
                msgs.put(msg)
            } while (cursor.moveToNext() && msgs.length() < number)
        }
        cursor.close()
        return msgs
    }
}
