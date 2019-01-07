package com.ztgeng.smssenderkotlin

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.AppCompatButton
import android.util.Log
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.util.*

class MainActivity : AppCompatActivity() {

    private val permissionRequestCode = 1234

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button: AppCompatButton = findViewById(R.id.button)
        button.setOnClickListener {
//            Log.d("gengz", readSms(5))
            sendPost("http://192.168.0.25:8000", readSms(3))
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_SMS), permissionRequestCode)
        } else {
            Log.d("gengz", "Already granted permission")
        }

//        Log.d("gengz", readSms(5))
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            permissionRequestCode -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("gengz", "Permission granted")
                } else {
                    Log.d("gengz", "Permission denied")
                }
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

    private fun sendPost(url: String, jsonArray: JSONArray) {
        val queue = Volley.newRequestQueue(this)
        val request = object : JsonRequest<String>(
                Request.Method.POST,
                url,
                jsonArray.toString(),
                Response.Listener<String> { response -> Log.d("gengz", response.toString()) },
                Response.ErrorListener { error -> Log.d("gengz", error.message) }) {
            override fun parseNetworkResponse(response: NetworkResponse): Response<String> {
                val parsed: String = try {
                    String(response.data, Charset.forName(HttpHeaderParser.parseCharset(response.headers)))
                } catch (e: UnsupportedEncodingException) {
                    String(response.data, Charset.defaultCharset())
                }

                return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response))
            }
        }
        queue.add(request)
    }
}
