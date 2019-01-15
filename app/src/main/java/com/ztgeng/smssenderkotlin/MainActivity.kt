package com.ztgeng.smssenderkotlin

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.AppCompatEditText
import android.util.Log
import android.widget.Toast
import com.google.firebase.iid.FirebaseInstanceId

const val PREFS_NAME = "SmsSender"
const val SERVER_NAME = "server"

class MainActivity : AppCompatActivity() {

    private val permissionRequestCode = 1234

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val editText: AppCompatEditText = findViewById(R.id.server_input)
        editText.setText(getSharedPreferences(PREFS_NAME, MODE_PRIVATE).getString(SERVER_NAME, ""))

        val button: AppCompatButton = findViewById(R.id.button)
        button.setOnClickListener {
            val url = editText.text?.toString()
            if (!url.isNullOrBlank()) {
                getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().putString(SERVER_NAME, url).apply()
                getFCMToken()
            } else {
                Toast.makeText(this, "Invalid url", Toast.LENGTH_SHORT).show()
            }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_SMS), permissionRequestCode)
        }
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

    private fun getFCMToken() {
        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener { task ->
            run {
                if (!task.isSuccessful) {
                    Log.d("gengz", "Fail to get FCM token!")
                    return@run
                }
                val token = task.result?.token
                Log.d("gengz", "Get FCM token: $token")
                NetworkClient.getInstance(this.applicationContext).sendToken(this, token ?: return@run)
            }
        }
    }
}
