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
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import java.util.*

class MainActivity : AppCompatActivity() {

    private val permissionRequestCode = 1234

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button: AppCompatButton = findViewById(R.id.button)
        button.setOnClickListener {
//            Log.d("gengz", readSms(5).joinToString())
            sendPost("http://192.168.0.25:8000", readSms(8).joinToString())
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_SMS), permissionRequestCode)
        } else {
            Log.d("gengz", "Already granted permission")
        }

//        Log.d("gengz", readSms(5).joinToString())
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

    private fun readSms(number: Int): List<String> {
        val msgs = mutableListOf<String>()
        val cursor = contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, null) ?: return msgs
        if (cursor.moveToFirst()) {
            do {
                var msg = "{\n"
                for (i in 0 until cursor.columnCount) {
                    msg += when (cursor.getColumnName(i)) {
                        "address" -> "  \"number\": \" ${cursor.getString(i)}\",\n"
                        "date" -> "  \"time\": \" ${Date(cursor.getLong(i))}\",\n"
                        "body" -> "  \"message\": \" ${cursor.getString(i)}\"\n"
                        else -> ""
                    }
                }
                msg += "}"
                msgs.add(msg)
            } while (cursor.moveToNext() && msgs.size < number)
        }
        cursor.close()
        return msgs
    }

    private fun sendPost(url: String, json: String) {
        val queue = Volley.newRequestQueue(this)
        // Request a string response from the provided URL.
        val stringRequest = object : StringRequest(
                Request.Method.POST,
                url,
                Response.Listener<String> { response ->
                    Log.d("gengz", response)
                },
                Response.ErrorListener { Log.d("gengz", "error!") }) {
            override fun getBodyContentType() = "application/json; charset=utf-8"
            override fun getBody() = json.toByteArray()
        }

        // Add the request to the RequestQueue.
        queue.add(stringRequest)
    }
}
