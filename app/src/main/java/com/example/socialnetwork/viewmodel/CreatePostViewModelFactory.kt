package com.example.socialnetwork.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.socialnetwork.repository.DefaultMainRepository
import javax.inject.Inject

class CreatePostViewModelFactory @Inject constructor(
    private val defaultMainRepository: DefaultMainRepository,
    private val application: Application
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CreatePostViewModel(defaultMainRepository,application) as T
    }
}