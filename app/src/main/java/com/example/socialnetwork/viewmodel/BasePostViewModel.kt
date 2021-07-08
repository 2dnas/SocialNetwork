package com.example.socialnetwork.viewmodel

import android.util.EventLog
import android.util.EventLogTags
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialnetwork.data.model.Post
import com.example.socialnetwork.data.model.User
import com.example.socialnetwork.other.Event
import com.example.socialnetwork.other.UiState
import com.example.socialnetwork.repository.DefaultMainRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

open class BasePostViewModel @Inject constructor(
    private val repository: DefaultMainRepository
)  : ViewModel() {

    private val _likePostStatus = MutableLiveData<Event<UiState<Boolean>>>()
    val likePostStatus : LiveData<Event<UiState<Boolean>>> = _likePostStatus

    private val _deletePostStatus = MutableLiveData<Event<UiState<Post>>>()
    val deletePostStatus : LiveData<Event<UiState<Post>>> = _deletePostStatus

    private val _likedByUsers = MutableLiveData<Event<UiState<List<User>>>>()
    val likedByUsers : LiveData<Event<UiState<List<User>>>> = _likedByUsers


    open lateinit var posts : LiveData<Event<UiState<List<Post>>>>

    open fun getPosts(uid : String = ""){}

    fun getUsers(uids : List<String>) {
        _likedByUsers.postValue(Event(UiState.Loading()))
        viewModelScope.launch {
            val result = repository.getUsers(uids)
            _likedByUsers.postValue(Event(result))

        }
    }

    fun toggleLikeForPost(post : Post) {
        _likePostStatus.postValue(Event(UiState.Loading()))
        viewModelScope.launch {
            val result = repository.toggleLikeForPosts(post)
            _likePostStatus.postValue(Event(result))

        }
    }

    fun deletePost(post: Post) {
        _deletePostStatus.postValue(Event(UiState.Loading()))
        viewModelScope.launch {
            val result = repository.deletePost(post)
            _deletePostStatus.postValue(Event(result))

        }
    }
}