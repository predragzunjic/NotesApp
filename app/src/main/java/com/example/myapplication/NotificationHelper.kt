package com.example.myapplication

import android.annotation.TargetApi
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.myapplication.data.db.CommitmentDatabase
import com.example.myapplication.data.repositories.CommitmentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*


class NotificationHelper(base: Context?) : ContextWrapper(base) {
    private val channelID = "channelID"
    private val channelName = "Channel Name"
    private lateinit var repository: CommitmentRepository

    private var mManager: NotificationManager? = null

    init{
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val database = CommitmentDatabase(this)
            repository = CommitmentRepository(database)
            createChannel()
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        val channel = NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH)
        getManager()!!.createNotificationChannel(channel)
    }

    fun getManager(): NotificationManager? {
        if (mManager == null) {
            mManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        }
        return mManager
    }

    /*fun getChannelNotification(): NotificationCompat.Builder? {
        lateinit var titleDateTuple: CommitmentDao.TitleDateTuple


        CoroutineScope(Dispatchers.Main).launch {
            titleDateTuple = repository.getRandomTitleDate()
        }.invokeOnCompletion {
            val title = titleDateTuple.title
            val date = titleDateTuple.date

            return NotificationCompat.Builder(applicationContext, channelID)
                .setContentTitle(title)
                .setContentText(date)
                .setSmallIcon(R.drawable.ic_add)
        }
        //val preference = getSharedPreferences(resources.getString(R.string.app_name), Context.MODE_PRIVATE)
        //val title = preference.getString("TITLE", "0")
        //val content = preference.getString("CONTENT", "0")



    }*/

    suspend fun getChannelNotification(): NotificationCompat.Builder = withContext(Dispatchers.IO){
        val titleDateTuple = repository.getRandomTitleDate()


        val title = titleDateTuple.title
        val date = titleDateTuple.date

        return@withContext NotificationCompat.Builder(applicationContext, channelID)
            .setContentTitle(title)
            .setContentText("Your commitment $title is sitting unfinished since $date")
            .setSmallIcon(R.drawable.ic_notification)
    }
}