package com.example.myapplication.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.myapplication.data.db.entities.Commitment

@Dao
interface CommitmentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(commitment: Commitment)

    @Query("UPDATE commitment_table SET isItDone = 1 WHERE `id` = :id")
    suspend fun update(id: Int?)

    @Query("UPDATE commitment_table SET `order` = :id2  WHERE `id` = :id1")
    suspend fun updateOrder(id1: Int?, id2: Int?)

    @Delete
    suspend fun delete(commitment: Commitment)

    @Query("SELECT * FROM commitment_table WHERE isItDone = :isItDone ORDER BY `order`")
    fun getCommitments(isItDone: Int): LiveData<List<Commitment>>

    @Query("DELETE FROM commitment_table WHERE isItDone = :isItDone")
    suspend fun deleteCommitments(isItDone: Int)

    @Query("SELECT title, date FROM commitment_table WHERE isItDone = 0 ORDER BY RANDOM() LIMIT 1;")
    suspend fun getRandomTitleDate(): TitleDateTuple

    data class TitleDateTuple(
        @ColumnInfo(name = "title") val title: String?,
        @ColumnInfo(name = "date") val date: String?)
}
