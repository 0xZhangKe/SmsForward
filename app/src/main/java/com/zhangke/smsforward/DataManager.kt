package com.zhangke.smsforward

import android.content.Context

object DataManager {

    private const val SP_NAME = "url_config"
    private const val KEY_URL = "url"

    fun saveUrl(url: String) {
        val sp = appContext.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
        sp.edit()
            .putString(KEY_URL, url)
            .apply()
    }

    fun getUrl(): String? {
        val sp = appContext.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
        return sp.getString(KEY_URL, null)
    }
}