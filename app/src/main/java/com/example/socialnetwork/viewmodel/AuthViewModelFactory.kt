package com.example.socialnetwork.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.socialnetwork.repository.AuthRepository
import javax.inject.Inject

class AuthViewModelFactory @Inject constructor(
    private val repository: AuthRepository,
    private val application: Application,
)  : ViewModelProvider.NewInstanceFactory(){

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AuthViewModel(repository,application) as T
    }
}