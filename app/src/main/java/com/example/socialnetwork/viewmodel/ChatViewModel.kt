package com.example.socialnetwork.viewmodel

import android.util.EventLog
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialnetwork.data.model.Message
import com.example.socialnetwork.other.Event
import com.example.socialnetwork.other.UiState
import com.example.socialnetwork.repository.DefaultMainRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChatViewModel @Inject constructor(
    private val repository: DefaultMainRepository
) : ViewModel(){

    private var _messages : MutableLiveData<Event<UiState<MutableList<Message>>>> = MutableLiveData<Event<UiState<MutableList<Message>>>>()
    val messages : LiveData<Event<UiState<MutableList<Message>>>> = _messages


    fun getMessages(uid : String) {
        _messages.postValue(Event(UiState.Loading()))
        viewModelScope.launch {
            val result = repository.getChatMessages(uid)
            _messages.postValue(Event(result))
        }
    }

    fun addChatMessage(message : String, uid: String) {
        viewModelScope.launch {
            val result = repository.addChatMessage(message,uid)
            _messages.postValue(Event(result))

        }
    }



}