package com.ztgeng.smssenderkotlin

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

    /**
     * 当获取新的FCM Token，则向服务端更新。
     */
    override fun onNewToken(token: String?) {
        Log.d("gengz", "On new token: $token")
        if (!token.isNullOrBlank()) {
            NetworkClient.getInstance(this.applicationContext).sendToken(this, token)
        }
    }

    /**
     * 处理{@code remoteMessage.collapseKey}值为“fetch_sms”的消息：
     *  - 若{@code remoteMessage.data}存在“server”键，则以其值更新储存在SharedPreferences中的服务器地址；
     *  - 若{@code remoteMessage.data}存在“number”键，则以其值作为读取短信条数，否则默认读取5条；
     *  - 读取短信并发送到储存的服务器地址。
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        Log.d("gengz", "From: ${remoteMessage?.from}")
        remoteMessage?.data?.isNotEmpty()?.let { Log.d("gengz", "Data: ${remoteMessage.data}") }
        remoteMessage?.notification?.let { Log.d("gengz", "Message: ${it.title} - ${it.body}") }

        when (remoteMessage?.collapseKey) {
            FETCH_SMS_KEY -> {
                val url = remoteMessage.data?.get(SERVER_IP_KEY)
                url?.let { saveServerName(this, it) }

                NetworkClient.getInstance(this.applicationContext).sendSms(
                        this, readSms(remoteMessage.data?.get(SMS_NUMBER_KEY)?.toInt() ?: DEFAULT_SMS_NUMBER))
            }
        }
    }

    /**
     * 读取最多{@code number}条最新短信，转为JSON。格式为：
     * ```
     * [
     *   {
     *     number: ..., // 短信发送方号码
     *     time: ..., // 发送时间，格式为：dow mon dd hh:mm:ss zzz yyyy
     *     message: ... // 短信文本
     *   },
     *   ...
     * ]
     * ```
     */
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
