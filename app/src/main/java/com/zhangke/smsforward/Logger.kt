package com.zhangke.smsforward

import android.util.Log

object Logger {

    private const val TAG = "sms_forward"

    fun i(message: String) {
        Log.i(TAG, message)
    }

    fun e(e: Throwable) {
        Log.e(TAG, e.stackTraceToString())
    }

    fun e(message: String, e: Throwable? = null) {
        Log.e(TAG, "$message; ${e?.stackTraceToString()}")
    }
}