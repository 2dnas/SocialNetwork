package com.example.socialnetwork.viewmodel

import android.app.Application
import android.net.Uri
import android.os.health.UidHealthStats
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialnetwork.R
import com.example.socialnetwork.data.model.ProfileUpdate
import com.example.socialnetwork.data.model.User
import com.example.socialnetwork.other.Event
import com.example.socialnetwork.other.UiState
import com.example.socialnetwork.repository.DefaultMainRepository
import com.example.socialnetwork.utils.Constants.MIN_USERNAME_LENGTH
import kotlinx.coroutines.launch
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    val repository: DefaultMainRepository,
    val application : Application
) : ViewModel() {

    private val _updateProfileStatus = MutableLiveData<Event<UiState<Any>>>()
    val updateProfileStatus : LiveData<Event<UiState<Any>>> = _updateProfileStatus

    private val _getUserStatus = MutableLiveData<Event<UiState<User?>>>()
    val getUserStatus : LiveData<Event<UiState<User?>>> = _getUserStatus

    private val _currentImageUri = MutableLiveData<Uri>()
    val currentImageUri : LiveData<Uri> = _currentImageUri

    fun updateProfile(profileUpdate: ProfileUpdate) {
        if(profileUpdate.username.isEmpty() || profileUpdate.description.isEmpty()) {
            val error = application.getString(R.string.error_input_empty)
            _updateProfileStatus.postValue(Event(UiState.Error(error)))
        } else if ( profileUpdate.username.length < MIN_USERNAME_LENGTH) {
            val error = application.getString(R.string.error_username_too_short)
            _updateProfileStatus.postValue(Event(UiState.Error(error)))
        }else {
            _updateProfileStatus.postValue(Event(UiState.Loading()))
            viewModelScope.launch {
                val result = repository.updateProfile(profileUpdate)
                _updateProfileStatus.postValue(Event(result))
            }
        }
    }

    fun getUser(uid : String) {
        _getUserStatus.postValue(Event(UiState.Loading()))
        viewModelScope.launch {
            val result = repository.getUser(uid)
            _getUserStatus.postValue(Event(result))
        }
    }

    fun setCurrentImage(uri: Uri) {
        _currentImageUri.postValue(uri)
    }

}