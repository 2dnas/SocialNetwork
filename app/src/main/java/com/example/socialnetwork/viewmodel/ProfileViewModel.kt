package com.example.socialnetwork.viewmodel
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

class ProfileViewModel @Inject constructor(
    private val repository: DefaultMainRepository
) : ViewModel() {

    private val _myProfile = MutableLiveData<Event<UiState<User?>>>()
    val myProfile : LiveData<Event<UiState<User?>>> = _myProfile

    private val _followStatus = MutableLiveData<Event<UiState<Boolean>>>()
    val followStatus : LiveData<Event<UiState<Boolean>>> = _followStatus

    private val _posts = MutableLiveData<Event<UiState<List<Post>>>>()
    val posts : LiveData<Event<UiState<List<Post>>>> = _posts

    fun getPosts(uid : String) {
        _posts.postValue(Event(UiState.Loading()))
        viewModelScope.launch {
            val result = repository.getPostsForProfile(uid)
            _posts.postValue(Event(result))
        }
    }

    fun toggleFollowForUser(uid : String) {
        _followStatus.postValue(Event(UiState.Loading()))
        viewModelScope.launch {
            val result = repository.toggleFollowForUser(uid)
            _followStatus.postValue(Event(result))
        }
    }

    fun loadProfile(uid : String) {
        _myProfile.postValue(Event(UiState.Loading()))
        viewModelScope.launch {
            val result = repository.getUser(uid)
            _myProfile.postValue(Event(result))
        }
    }


}