package com.example.socialnetwork.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialnetwork.data.model.Post
import com.example.socialnetwork.other.Event
import com.example.socialnetwork.other.UiState
import com.example.socialnetwork.repository.DefaultMainRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val repository : DefaultMainRepository,
) : BasePostViewModel(repository) {

    private val _posts = MutableLiveData<Event<UiState<List<Post>>>>()

    override var posts: LiveData<Event<UiState<List<Post>>>> = _posts

    init {
        getPosts()
    }

    override fun getPosts(uid: String) {
        _posts.postValue(Event(UiState.Loading()))
        viewModelScope.launch {
            val result = repository.getPostsFromFollows()
            _posts.postValue(Event(result))
        }
    }

}