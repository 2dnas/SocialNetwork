package com.example.socialnetwork.base

import androidx.fragment.app.DialogFragment

open class BaseDialogFragment : DialogFragment(){

    private val presentationComponent by lazy {
        (requireContext() as BaseActivity).activityComponent.newPresentationComponent()
    }

    protected val injector get() = presentationComponent
}