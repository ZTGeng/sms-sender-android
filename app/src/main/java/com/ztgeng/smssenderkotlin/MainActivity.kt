package com.ztgeng.smssenderkotlin

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.AppCompatEditText
import android.util.Log
import android.widget.Toast
import com.google.firebase.iid.FirebaseInstanceId

class MainActivity : AppCompatActivity() {

    private val permissionRequestCode = 1234
    private lateinit var editText: AppCompatEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editText = findViewById(R.id.server_input)
        editText.setText(getServerName(this))

        val button: AppCompatButton = findViewById(R.id.button)
        button.setOnClickListener {
            val url = editText.text?.toString()
            if (!url.isNullOrBlank()) {
                saveServerName(this, url)
                updateFCMToken()
            } else {
                Toast.makeText(this, "Invalid url", Toast.LENGTH_SHORT).show()
            }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_SMS), permissionRequestCode)
        }

        // Android 4.4 需要这么做一下来触发授权申请
        val cursor = contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, null)
        cursor?.close()
    }

    override fun onResume() {
        super.onResume()
        editText.setText(getServerName(this))
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

    /**
     * 主动获取本机FCM Token并向服务端更新。
     */
    private fun updateFCMToken() {
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
