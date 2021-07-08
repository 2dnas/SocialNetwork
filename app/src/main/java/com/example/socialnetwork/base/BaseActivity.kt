package com.example.socialnetwork.base

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import com.example.socialnetwork.App
import com.example.socialnetwork.di.activity.ActivityModules

open class BaseActivity : AppCompatActivity() {

    val appComponent get () = (application as App).appComponent

    val activityComponent by lazy {
        appComponent.newActivityComponent()
    }

    private val presentationComponent by lazy {
        activityComponent.newPresentationComponent()
    }

    protected val injector get() = presentationComponent
}