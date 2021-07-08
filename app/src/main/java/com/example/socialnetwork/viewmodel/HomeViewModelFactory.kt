package com.example.socialnetwork.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.socialnetwork.repository.DefaultMainRepository
import javax.inject.Inject

class HomeViewModelFactory @Inject constructor(
    private val repository: DefaultMainRepository,

) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HomeViewModel(repository) as T
    }
}