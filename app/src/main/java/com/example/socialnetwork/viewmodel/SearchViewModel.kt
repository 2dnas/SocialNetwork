package com.example.socialnetwork.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialnetwork.data.model.User
import com.example.socialnetwork.other.Event
import com.example.socialnetwork.other.UiState
import com.example.socialnetwork.repository.DefaultMainRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class SearchViewModel @Inject constructor(
    private val repository: DefaultMainRepository
) : ViewModel() {

    private val _searchResult = MutableLiveData<Event<UiState<List<User>>>>()
    val searchResult : LiveData<Event<UiState<List<User>>>> = _searchResult

    fun searchUser(query : String ) {
        if(query.isEmpty()) return

        _searchResult.postValue(Event(UiState.Loading()))
        viewModelScope.launch {
            val result = repository.searchUser(query)
            _searchResult.postValue(Event(result))
        }
    }
}