package com.example.myapplication.data.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.data.repositories.CommitmentRepository

class CommitmentViewModelFactory(
    private val repository: CommitmentRepository
): ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CommitmentViewModel(repository) as T
    }
}