package com.example.socialnetwork.other

import android.app.Activity
import android.content.Intent
import com.example.socialnetwork.base.BaseActivity
import java.lang.Exception


inline fun <T> safeCall(action: () -> UiState<T>) : UiState<T> {
    return try {
        action()
    } catch (e : Exception){
        UiState.Error(e.message ?: "An Unknown message occurred")
    }
}


fun BaseActivity.goOnActivity(activity: Class<*>) {
    this.startActivity(Intent(this,activity))
}