package com.example.myapplication.data.viewmodels

import android.icu.text.CaseMap
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.data.db.CommitmentDao
import com.example.myapplication.data.db.entities.Commitment
import com.example.myapplication.data.repositories.CommitmentRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CommitmentViewModel (
    private val repository: CommitmentRepository): ViewModel(){

        private lateinit var commitments: LiveData<List<Commitment>>
        private lateinit var finishedCommitments: LiveData<List<Commitment>>
        private lateinit var titleDateTuple: CommitmentDao.TitleDateTuple
        //we use .Main thread because room provides main safety

        fun getCommitmentsLV(): LiveData<List<Commitment>>{
            return commitments
        }

        fun getFinishedCommitmentsLV(): LiveData<List<Commitment>>{
            return finishedCommitments
        }

        fun insert(commitment: Commitment) = CoroutineScope(Dispatchers.Main).launch {
            repository.insert(commitment)
        }

        fun update(id: Int?) = CoroutineScope(Dispatchers.Main).launch {
            repository.update(id)
        }

        fun updateOrder(id1: Int?, id2: Int?) = CoroutineScope(Dispatchers.Main).launch {
            repository.updateOrder(id1, id2)
        }

        fun delete(commitment: Commitment) = CoroutineScope(Dispatchers.Main).launch {
            repository.delete(commitment)
        }

        fun getCommitments(isItDone: Int){
            if(isItDone == 0)
                commitments = repository.getCommitments(isItDone)
            else
                finishedCommitments = repository.getCommitments(isItDone)
        }

        fun deleteCommitments(isItDone: Int) = CoroutineScope(Dispatchers.Main).launch {
            repository.deleteCommitments(isItDone)
        }

        fun getRandomTitleDate() = CoroutineScope(Dispatchers.Main).launch {
            titleDateTuple = repository.getRandomTitleDate()
        }

        fun getRandomTitleDateS(): CommitmentDao.TitleDateTuple{
            return titleDateTuple
        }
    }
