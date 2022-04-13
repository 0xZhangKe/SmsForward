package com.zhangke.smsforward

import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors

object Pusher {

    private val executor = Executors.newCachedThreadPool()

    fun push(message: String) {
        val url = DataManager.getUrl() ?: return
        performGetHttpRequest(url + message)
    }

    private fun performGetHttpRequest(urlString: String) {
        try {
            executor.execute {
                try {
                    val url = URL(urlString)
                    val connection = url.openConnection() as HttpURLConnection
                    connection.requestMethod = "GET"
                    connection.connect()
                    val code = connection.responseCode
                    if (code in 200..299) {
                        Logger.i("code=$code, response=${String(connection.inputStream.readBytes())}")
                    } else {
                        Logger.i("code=$code, response=${String(connection.errorStream.readBytes())}")
                    }
                } catch (e: Throwable) {
                    Logger.e(e)
                }
            }
        } catch (e: Throwable) {
            Logger.e(e)
        }
    }
}