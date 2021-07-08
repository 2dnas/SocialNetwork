package com.example.socialnetwork.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialnetwork.data.model.Comment
import com.example.socialnetwork.other.Event
import com.example.socialnetwork.other.UiState
import com.example.socialnetwork.repository.DefaultMainRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class CommentViewModel @Inject constructor(
    private val repository: DefaultMainRepository
) : ViewModel(){

    private val _createCommentStatus = MutableLiveData<Event<UiState<Comment>>>()
    val createCommentStatus : LiveData<Event<UiState<Comment>>> = _createCommentStatus

    private val _deleteCommentStatus = MutableLiveData<Event<UiState<Comment>>>()
    val deleteCommentStatus : LiveData<Event<UiState<Comment>>> = _deleteCommentStatus

    private val _commentForPosts = MutableLiveData<Event<UiState<List<Comment>>>>()
    val commentForPosts : LiveData<Event<UiState<List<Comment>>>> = _commentForPosts


    fun createComment(commentText: String, postId : String) {
        if(commentText.isEmpty()) return
        _createCommentStatus.postValue(Event(UiState.Loading()))
        viewModelScope.launch {
            val result = repository.createComment(commentText,postId)
            _createCommentStatus.postValue(Event(result))
        }
    }

    fun deleteComment(comment: Comment) {
        _deleteCommentStatus.postValue(Event(UiState.Loading()))
        viewModelScope.launch {
            val result = repository.deleteComment(comment)
            _deleteCommentStatus.postValue(Event(result))
        }
    }

    fun getCommentsForPost(postId : String) {
        _commentForPosts.postValue(Event(UiState.Loading()))
        viewModelScope.launch {
            val result = repository.getComments(postId)
            _commentForPosts.postValue(Event(result))
        }
    }

}