package com.example.socialnetwork.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialnetwork.R
import com.example.socialnetwork.other.Event
import com.example.socialnetwork.other.UiState
import com.example.socialnetwork.repository.DefaultMainRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class CreatePostViewModel @Inject constructor(
    private val defaultMainRepository: DefaultMainRepository,
    private val application: Application
) : ViewModel() {

    private val _createPostStatus = MutableLiveData<Event<UiState<Any>>>()
    val createPostStatus : LiveData<Event<UiState<Any>>> = _createPostStatus

    fun createPost(uri: Uri, text : String) {
        if(text.isEmpty()){
            val error = application.getString(R.string.error_input_empty)
            _createPostStatus.postValue(Event(UiState.Error(error)))
        } else {
            _createPostStatus.postValue(Event(UiState.Loading()))
            viewModelScope.launch {
                val result = defaultMainRepository.createPost(uri,text)
                _createPostStatus.postValue(Event(result))
            }
        }
    }
}