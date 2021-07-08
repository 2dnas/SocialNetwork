package com.example.socialnetwork.di.presentation

import androidx.fragment.app.Fragment
import com.example.socialnetwork.ui.activities.AuthActivity
import com.example.socialnetwork.ui.dialogs.CommentDialog
import com.example.socialnetwork.ui.fragments.*
import dagger.Subcomponent


@PresentationScope
@Subcomponent
interface PresentationComponent {
    fun inject(activity : AuthActivity)
    fun inject(fragment: RegisterFragment)
    fun inject(fragment: LoginFragment)
    fun inject(fragment: CreatePostFragment)
    fun inject(fragment: HomeFragment)
    fun inject(fragment: SearchFragment)
    fun inject(fragment: SettingsFragment)
    fun inject(fragment: ProfileFragment)
    fun inject(fragment: OthersProfileFragment)
    fun inject(chatFragment: ChatFragment)
    fun inject(commentDialog: CommentDialog) {

    }
}