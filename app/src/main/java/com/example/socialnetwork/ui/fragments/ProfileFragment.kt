package com.example.socialnetwork.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.RequestManager
import com.example.socialnetwork.R
import com.example.socialnetwork.adapter.PostAdapter
import com.example.socialnetwork.base.BaseFragment
import com.example.socialnetwork.other.UiState
import com.example.socialnetwork.utils.snackbar
import com.example.socialnetwork.utils.visible
import com.example.socialnetwork.viewmodel.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_profile.*
import javax.inject.Inject

class ProfileFragment : BaseFragment(R.layout.fragment_profile) {

    @Inject lateinit var viewModel : ProfileViewModel
    @Inject lateinit var auth : FirebaseAuth
    @Inject lateinit var postAdapter : PostAdapter
    @Inject lateinit var glide : RequestManager

    override fun onCreate(savedInstanceState: Bundle?) {
        injector.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        initObservers()
        viewModel.getPosts(auth.uid!!)


        btnToggleFollow.isVisible = false
        viewModel.loadProfile(auth.uid!!)
    }

    private fun setupRecyclerView() {
        rvPosts.apply {
            adapter = postAdapter
            layoutManager = LinearLayoutManager(requireContext())

        }
    }

    private fun initObservers() {
        viewModel.posts.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let {result ->
                when(result) {
                    is UiState.Loading -> profilePostsProgressBar.visible(true)
                    is UiState.Error -> {
                        profilePostsProgressBar.visible(false)
                        snackbar(result.message ?: "Unknown Error")
                    }
                    is UiState.Success -> {
                        profilePostsProgressBar.visible(false)
                        postAdapter.posts = result.data!!
                    }
                }
            }
        })


        viewModel.myProfile.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let { result ->
                when(result) {
                    is UiState.Loading -> myProfileProgressBar.visible(true)
                    is UiState.Error -> {
                        myProfileProgressBar.visible(false)
                        snackbar(result.message ?: "Unknown Error")
                    }
                    is UiState.Success -> {
                        myProfileProgressBar.visible(false)
                        tvUsername.text = result.data?.username
                        tvProfileDescription.text = if(result.data?.descriptor?.isEmpty()!!){
                            getString(R.string.no_description)
                        } else result.data.descriptor
                        glide.load(result.data.profilePictureUrl).into(ivProfileImage)
                    }
                }
            }
        })

    }

}