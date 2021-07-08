package com.example.socialnetwork.repository

import com.example.socialnetwork.data.model.User
import com.example.socialnetwork.other.UiState
import com.example.socialnetwork.other.safeCall
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject


class AuthRepository @Inject constructor(private val auth : FirebaseAuth, private val firestore : FirebaseFirestore){

    suspend fun register(
        email : String,
        username : String,
        password : String,
    ) : UiState<AuthResult> {
        return withContext(Dispatchers.IO){
            safeCall {
                val result = auth.createUserWithEmailAndPassword(email,password).await()
                val uid = result.user?.uid!!
                val user = User(uid,username)
                firestore.collection("users").document(uid).set(user).await()
                UiState.Success(result)
            }
        }
    }

    suspend fun login(email: String,password: String) : UiState<AuthResult> {
        return withContext(Dispatchers.IO){
            safeCall {
                val result = auth.signInWithEmailAndPassword(email,password).await()
                UiState.Success(result)
            }
        }

    }
}