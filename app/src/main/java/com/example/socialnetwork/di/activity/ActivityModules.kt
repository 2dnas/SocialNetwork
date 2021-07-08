package com.example.socialnetwork.di.activity

import com.example.socialnetwork.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides


@Module
class ActivityModules {

    @ActivityScope
    @Provides
    fun provideAuthRepository(auth : FirebaseAuth,firestore : FirebaseFirestore) = AuthRepository(auth,firestore)

}