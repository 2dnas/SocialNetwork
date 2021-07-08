package com.example.socialnetwork.base

import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import com.bumptech.glide.RequestManager
import com.example.socialnetwork.adapter.PostAdapter
import javax.inject.Inject

open class BaseFragment(fragment: Int) : Fragment(fragment) {

    private val presentationComponent by lazy {
        (requireContext() as BaseActivity).activityComponent.newPresentationComponent()
    }

    protected val injector get() = presentationComponent
}