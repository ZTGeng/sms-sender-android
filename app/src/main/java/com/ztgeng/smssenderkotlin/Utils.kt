package com.ztgeng.smssenderkotlin

import android.content.Context
import android.support.v7.app.AppCompatActivity

private const val PREFS_NAME = "SmsSender"
private const val SERVER_NAME = "server"

/**
 * 从SharedPreferences读取服务器地址，形如“x.x.x.x:port”。
 * 若不存在储存值，则返回{@code defaultValue}。
 * 若未提供{@code defaultValue}，则返回null。
 *
 * @return 服务器地址，或null。
 */
fun getServerName(context: Context, defaultValue: String? = null): String?
        = context.getSharedPreferences(PREFS_NAME, AppCompatActivity.MODE_PRIVATE).getString(SERVER_NAME, defaultValue)

/**
 * 将服务器地址存入SharedPreferences。地址应符合“x.x.x.x:port”格式，不含“http”等协议名。
 */
fun saveServerName(context: Context, value: String)
        = context.getSharedPreferences(PREFS_NAME, AppCompatActivity.MODE_PRIVATE).edit().putString(SERVER_NAME, value).apply()
