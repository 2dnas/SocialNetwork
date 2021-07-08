package com.example.socialnetwork.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.transition.ChangeBounds
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.RequestManager
import com.example.socialnetwork.R
import com.example.socialnetwork.adapter.PostAdapter
import com.example.socialnetwork.base.BaseFragment
import com.example.socialnetwork.data.model.User
import com.example.socialnetwork.other.UiState
import com.example.socialnetwork.utils.snackbar
import com.example.socialnetwork.utils.visible
import com.example.socialnetwork.viewmodel.ProfileViewModel
import kotlinx.android.synthetic.main.fragment_others_profile.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.btnToggleFollow
import kotlinx.android.synthetic.main.fragment_profile_anim.view.*
import kotlinx.coroutines.currentCoroutineContext
import javax.inject.Inject

class OthersProfileFragment : BaseFragment(R.layout.fragment_others_profile) {

    @Inject
    lateinit var viewModel: ProfileViewModel

    @Inject
    lateinit var postAdapter: PostAdapter

    @Inject
    lateinit var glide: RequestManager


    private var curUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        injector.inject(this)
        super.onCreate(savedInstanceState)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
        setupRecyclerView()
        val uid: String = arguments?.getString("uid")!!
        viewModel.loadProfile(uid)
        viewModel.getPosts(uid)



        btnToggleFollowOther.setOnClickListener {
            viewModel.toggleFollowForUser(uid)
        }

        btnChatOther.setOnClickListener {
            findNavController().navigate(OthersProfileFragmentDirections.actionOthersProfileFragmentToChatFragment(uid))
        }
    }

    private fun initObservers() {
        viewModel.posts.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let {result ->
                when(result) {
                    is UiState.Loading -> profilePostsProgressBarOther.visible(true)
                    is UiState.Error -> {
                        profilePostsProgressBarOther.visible(false)
                        snackbar(result.message ?: "Unknown Error")
                    }
                    is UiState.Success -> {
                        postAdapter.posts = result.data!!
                    }
                }
            }
        })
        viewModel.myProfile.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let { result ->
                when (result) {
                    is UiState.Loading -> profilePostsProgressBarOther.visible(true)
                    is UiState.Error -> {
                        profilePostsProgressBarOther.visible(false)
                        snackbar(result.message ?: "Unknown Error")
                    }
                    is UiState.Success -> {
                        curUser = result.data
                        toggleFollowingButton(curUser!!)
                        profilePostsProgressBarOther.visible(false)
                        tvUsernameOther.text = result.data?.username
                        tvProfileDescriptionOther.text = if (result.data?.descriptor?.isEmpty()!!) {
                            getString(R.string.no_description)
                        } else result.data.descriptor
                        glide.load(result.data.profilePictureUrl).into(ivProfileImageOther)
                    }
                }
            }
        })
        viewModel.followStatus.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let { result ->
                when (result) {
                    is UiState.Error -> snackbar(result.message!!)
                    is UiState.Success -> {
                        curUser?.isFollowing = result.data!!
                        btnToggleFollowOther.apply {
                            if (curUser?.isFollowing!!) {

                                text = getString(R.string.unfollow)
                                setBackgroundColor(Color.RED)
                                setTextColor(Color.WHITE)
                            } else {
                                text = requireContext().getString(R.string.follow)
                                setBackgroundColor(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        R.color.colorAccent
                                    )
                                )
                                setTextColor(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        R.color.darkBackground
                                    )
                                )
                            }
                        }
                    }

                }
            }
        })

    }


    fun setupRecyclerView() {
        rvPostsOthers.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = postAdapter
        }
    }

    fun toggleFollowingButton(user: User) {
        btnToggleFollowOther.apply {

            if (user.isFollowing) {

                text = getString(R.string.unfollow)
                setBackgroundColor(Color.RED)
                setTextColor(Color.WHITE)
            } else {
                text = requireContext().getString(R.string.follow)
                setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.colorAccent
                    )
                )
                setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.darkBackground
                    )
                )
            }
        }
    }


}
