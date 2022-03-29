package com.example.myapplication.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.myapplication.data.db.entities.Commitment

@Database(
    entities = [Commitment::class],
    version = 3
)
abstract class CommitmentDatabase: RoomDatabase() {
    abstract fun getCommitmentDao(): CommitmentDao

    companion object{
        @Volatile//writes odma vidljivi drugim threadovima
        private var instance: CommitmentDatabase? = null

        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK){
            instance ?: createDatabase(context).also{ instance = it }
        }

        private fun createDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext,
                CommitmentDatabase::class.java, "CommitmentDB.db")
                .fallbackToDestructiveMigration()
                .build()
    }


}