package com.ztgeng.smssenderkotlin

import android.content.Context
import android.support.v7.app.AppCompatActivity

private const val PREFS_NAME = "SmsSender"
private const val SERVER_NAME = "server"

fun getServerName(context: Context, defaultValue: String? = null): String?
        = context.getSharedPreferences(PREFS_NAME, AppCompatActivity.MODE_PRIVATE).getString(SERVER_NAME, defaultValue)

fun saveServerName(context: Context, value: String)
        = context.getSharedPreferences(PREFS_NAME, AppCompatActivity.MODE_PRIVATE).edit().putString(SERVER_NAME, value).apply()
