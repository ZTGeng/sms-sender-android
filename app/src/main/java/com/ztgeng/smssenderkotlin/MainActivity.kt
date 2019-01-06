package com.ztgeng.smssenderkotlin

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import java.util.*

class MainActivity : AppCompatActivity() {

    private val permissionRequestCode = 1234

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_SMS), permissionRequestCode)
        } else {
            Log.d("gengz", "Already granted permission")
        }

        Log.d("gengz", readSms(5).joinToString())
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

    fun readSms(number: Int): List<String> {
        val msgs = mutableListOf<String>()
        val cursor = contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, null) ?: return msgs
        if (cursor.moveToFirst()) {
            do {
                var msg = "{\n"
                for (i in 0 until cursor.columnCount) {
                    msg += when (cursor.getColumnName(i)) {
                        "address" -> "  \"number\": \" ${cursor.getString(i)}\",\n"
                        "date" -> "  \"time\": \" ${Date(cursor.getLong(i))}\",\n"
                        "body" -> "  \"message\": \" ${cursor.getString(i)}\",\n"
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
}
