package com.ztgeng.smssenderkotlin

import android.content.Context
import android.util.Log
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.JsonRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import kotlin.jvm.Volatile

class NetworkClient(context: Context){
    companion object {
        @Volatile
        private var INSTANCE: NetworkClient? = null
        fun getInstance(context: Context) = INSTANCE ?: synchronized(this) {
            INSTANCE ?: NetworkClient(context).also {
                INSTANCE = it
            }
        }
    }

    private val requestQueue: RequestQueue by lazy {
        // applicationContext is key, it keeps you from leaking the
        // Activity or BroadcastReceiver if someone passes one in.
        Volley.newRequestQueue(context.applicationContext)
    }

    fun sendToken(url: String?, token: String) {
        sendString(url ?: return, token)
    }

    fun sendSms(url: String?, data: JSONArray) {
        sendJsonArray(url ?: return, data)
    }

    private fun sendJsonArray(url: String, jsonArray: JSONArray) {
        val request = object : JsonRequest<String>(
                Request.Method.POST,
                "http://$url/sms",
                jsonArray.toString(),
                Response.Listener<String> { response -> Log.d("gengz", response.toString()) },
                Response.ErrorListener { error -> Log.d("gengz", error.toString()) }) {
            override fun parseNetworkResponse(response: NetworkResponse): Response<String> {
                val parsed: String = try {
                    String(response.data, Charset.forName(HttpHeaderParser.parseCharset(response.headers)))
                } catch (e: UnsupportedEncodingException) {
                    String(response.data, Charset.defaultCharset())
                }

                return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response))
            }
        }
        requestQueue.add(request)
    }

    private fun sendString(url: String, data: String) {
        val request = object : StringRequest(
                Request.Method.POST,
                "http://$url/token",
                Response.Listener<String> { response -> Log.d("gengz", response.toString()) },
                Response.ErrorListener { error -> Log.d("gengz", error.toString()) }) {
            override fun getBodyContentType() = "text/plain; charset=utf-8"
            override fun getBody() = data.toByteArray()
        }
        requestQueue.add(request)
    }
}
