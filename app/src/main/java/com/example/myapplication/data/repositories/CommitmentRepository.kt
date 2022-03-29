package com.example.myapplication.data.repositories

import com.example.myapplication.data.db.CommitmentDatabase
import com.example.myapplication.data.db.entities.Commitment

class CommitmentRepository(
    private val db: CommitmentDatabase
) {

    suspend fun insert(item: Commitment) = db.getCommitmentDao().insert(item)

    suspend fun update(id: Int?) = db.getCommitmentDao().update(id)

    suspend fun updateOrder(id1: Int?, id2: Int?) = db.getCommitmentDao().updateOrder(id1, id2)

    suspend fun delete(item: Commitment) = db.getCommitmentDao().delete(item)

    fun getCommitments(isItDone: Int) = db.getCommitmentDao().getCommitments(isItDone)

    suspend fun deleteCommitments(isItDone: Int) = db.getCommitmentDao().deleteCommitments(isItDone)

    suspend fun getRandomTitleDate() = db.getCommitmentDao().getRandomTitleDate()
}