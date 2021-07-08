package com.example.socialnetwork.di.activity

import com.example.socialnetwork.di.presentation.PresentationComponent
import dagger.Subcomponent

@ActivityScope
@Subcomponent
interface ActivityComponent {
    fun newPresentationComponent() : PresentationComponent

}