package com.zhangke.smsforward

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.telephony.SmsMessage
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*

class DaemonService : Service() {

    private var receiver: SmsReceiver? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let { handleIntent(it) }
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()

        val activityIntent = Intent(this, MainActivity::class.java)
        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "Daemon"
            val channel =
                NotificationChannel(channelId, "Daemon", NotificationManager.IMPORTANCE_HIGH)
            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(channel)
            Notification.Builder(this, channelId)
        } else {
            Notification.Builder(this)
        }
        val notificationBuilder =
            builder.setContentIntent(PendingIntent.getActivity(this, 0, activityIntent, 0))
                .setContentTitle("Daemon 你")

        startForeground(42, notificationBuilder.build())

        val filter = IntentFilter()
        filter.addAction("android.provider.Telephony.SMS_RECEIVED")
        SmsReceiver().also {
            registerReceiver(it, filter)
            receiver = it
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        receiver?.let { unregisterReceiver(it) }
    }

    private fun handleIntent(intent: Intent) {
        val url = intent.getStringExtra("url") ?: return
        DataManager.saveUrl(url)
    }

    private class SmsReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent?) {
            val data = intent?.extras ?: return
            val pdus = data["pdus"] as? Array<*> ?: return
            pdus.forEach {
                val message = SmsMessage.createFromPdu(it as ByteArray)
                Logger.i(message?.toString().orEmpty())
                val pushBuilder = StringBuilder()
                message.timestampMillis?.let {
                    pushBuilder.append("date: ")
                    try {
                        val date = Date(it)
                        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ROOT)
                        pushBuilder.append(format.format(date))
                    } catch (e: Exception) {
                        pushBuilder.append(it.toString())
                    }
                    pushBuilder.append("; ")
                }
                message.originatingAddress?.let {
                    pushBuilder.append("address: ")
                    pushBuilder.append(it)
                    pushBuilder.append("; ")
                }
                message.messageBody?.let {
                    pushBuilder.append("content: ")
                    pushBuilder.append(it)
                }
                val pushText = pushBuilder.toString()
                Logger.i(pushText)
                Toast.makeText(context, pushText, Toast.LENGTH_LONG).show()
                Pusher.push(pushText)
            }
        }
    }
}