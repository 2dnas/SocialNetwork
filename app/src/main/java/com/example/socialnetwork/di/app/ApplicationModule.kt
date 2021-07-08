package com.example.socialnetwork.di.app

import android.app.Application
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.socialnetwork.R
import com.google.firebase.FirebaseError
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.FirebaseAuthKtxRegistrar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class ApplicationModule(private val application : Application) {

    @AppScope
    @Provides
    fun provideApplication() : Application = application

    @Provides
    @AppScope
    fun provideGlideInstance(
        application : Application
    )  = Glide.with(application).setDefaultRequestOptions(
        RequestOptions()
            .placeholder(R.drawable.ic_image)
            .error(R.drawable.ic_error)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
    )

    @Provides
    @AppScope
    fun provideFirebaseAuth() : FirebaseAuth = Firebase.auth

    @Provides
    @AppScope
    fun provideFirebaseFireStore() : FirebaseFirestore  = FirebaseFirestore.getInstance()

    @Provides
    @AppScope
    fun provideFirebaseStorage() : FirebaseStorage = Firebase.storage

    @Provides
    @AppScope
    fun provideFirebaseRealTime() : FirebaseDatabase = FirebaseDatabase.getInstance()


}