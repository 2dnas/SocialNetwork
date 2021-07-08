package com.example.socialnetwork

import android.app.Application
import com.example.socialnetwork.di.app.ApplicationModule
import com.example.socialnetwork.di.app.DaggerApplicationComponent
import com.google.firebase.FirebaseApp


class App : Application(){

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }

    public val appComponent by lazy {
        DaggerApplicationComponent.builder()
            .applicationModule(ApplicationModule(this))
            .build()
    }
}