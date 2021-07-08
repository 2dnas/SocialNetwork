package com.example.socialnetwork.di.app

import com.example.socialnetwork.di.activity.ActivityComponent
import com.example.socialnetwork.di.presentation.PresentationComponent
import dagger.Component

@AppScope
@Component(modules = [ApplicationModule::class])
interface ApplicationComponent {
    fun newActivityComponent() : ActivityComponent
}