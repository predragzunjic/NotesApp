package com.example.myapplication

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

const val notificationID = 1
const val channelID = "channel1"
const val titleExtra = "titleExtra"
const val messageExtra = "messageExtra"

class Notification: BroadcastReceiver() {
    //i am calling asynchronous function getChannelNotification, so i need to call it frorm
    override fun onReceive(context: Context, intent: Intent?) {
        CoroutineScope(Dispatchers.IO).launch {
            val notificationHelper = NotificationHelper(context)
            val nb = notificationHelper.getChannelNotification()
            notificationHelper.getManager()!!.notify(1, nb!!.build())
        }
    }
}