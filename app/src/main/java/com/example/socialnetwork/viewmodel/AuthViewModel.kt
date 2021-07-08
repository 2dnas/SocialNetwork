package com.example.socialnetwork.viewmodel

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialnetwork.R
import com.example.socialnetwork.other.Event
import com.example.socialnetwork.other.UiState
import com.example.socialnetwork.repository.AuthRepository
import com.example.socialnetwork.utils.Constants.MAX_USERNAME_LENGTH
import com.example.socialnetwork.utils.Constants.MIN_PASSWORD_LENGTH
import com.example.socialnetwork.utils.Constants.MIN_USERNAME_LENGTH
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class AuthViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val application: Application,
) : ViewModel() {

    
    private val _registerStatus = MutableLiveData<Event<UiState<AuthResult>>>()
    val registerStatus : LiveData<Event<UiState<AuthResult>>> = _registerStatus

    private val _loginStatus = MutableLiveData<Event<UiState<AuthResult>>>()
    val loginStatus : LiveData<Event<UiState<AuthResult>>> = _loginStatus

    fun login(email: String,password: String) {
        if(email.isEmpty() || password.isEmpty()) {
            val error = application.getString(R.string.error_input_empty)
            _loginStatus.postValue(Event(UiState.Error(error)))
        } else {
            _loginStatus.postValue(Event(UiState.Loading()))
            viewModelScope.launch {
                val result = repository.login(email,password)
                _loginStatus.postValue(Event(result))
            }
        }
    }

    fun register(email : String,username : String,password :String,repeatedPassword : String) {
        val error = if(email.isEmpty() || username.isEmpty() || password.isEmpty()){
            application.getString(R.string.error_input_empty)
        } else if(username.length < MIN_USERNAME_LENGTH) {
            application.getString(R.string.error_username_too_short)
        } else if(username.length > MAX_USERNAME_LENGTH) {
            application.getString(R.string.error_username_too_long)
        } else if(password.length < MIN_PASSWORD_LENGTH) {
            application.getString(R.string.error_password_too_short)
        } else if(password != repeatedPassword){
            application.getString(R.string.error_incorrectly_repeated_password)
        } else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            application.getString(R.string.error_not_a_valid_email)
        } else null

        error?.let {
            _registerStatus.postValue(Event(UiState.Error(it)))
        }
        _registerStatus.postValue(Event(UiState.Loading()))
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.register(email,username,password)
            _registerStatus.postValue(Event(result))
        }
    }

}