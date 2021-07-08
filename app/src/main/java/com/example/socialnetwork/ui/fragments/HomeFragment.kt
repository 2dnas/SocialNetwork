package com.example.socialnetwork.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.RequestManager
import com.example.socialnetwork.R
import com.example.socialnetwork.adapter.PostAdapter
import com.example.socialnetwork.adapter.UserAdapter
import com.example.socialnetwork.base.BaseFragment
import com.example.socialnetwork.other.UiState
import com.example.socialnetwork.ui.dialogs.DeletePostDialog
import com.example.socialnetwork.ui.dialogs.LikedByDialog
import com.example.socialnetwork.utils.snackbar
import com.example.socialnetwork.utils.visible
import com.example.socialnetwork.viewmodel.HomeViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_home.*
import javax.inject.Inject

class HomeFragment : BaseFragment(R.layout.fragment_home) {

    @Inject lateinit var glide : RequestManager
    @Inject lateinit var postAdapter : PostAdapter
    @Inject lateinit var viewModel : HomeViewModel
    @Inject lateinit var auth : FirebaseAuth

    private var currentIndex : Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        injector.inject(this)
        super.onCreate(savedInstanceState)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        initObservers()


        postAdapter.setOnDeleteClickListener { post ->
            DeletePostDialog().apply {
                setPositiveButtonListener {
                    viewModel.deletePost(post)
                }
            }.show(childFragmentManager,null)
        }

        postAdapter.setOnUserClickListener {
            findNavController().navigate(HomeFragmentDirections.globalActionToOthersProfileFragment(it))
        }

        postAdapter.setOnUserClickListener {
            findNavController().navigate(HomeFragmentDirections.globalActionToOthersProfileFragment(it))
        }

        postAdapter.setOnLikeClickListener { post , index ->
            currentIndex = index
            post.isLiked = !post.isLiked
            viewModel.toggleLikeForPost(post)
        }

        postAdapter.setOnLikedByClickListener {
            viewModel.getUsers(it.likedBy)
        }
        postAdapter.setOnCommentsClickListener {
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToCommentDialog(it.id)
            )
        }
    }

    private fun setupRecyclerView() {
        rvAllPosts.apply {
            adapter = postAdapter
            layoutManager = LinearLayoutManager(requireContext())
            itemAnimator = null
        }
    }

    fun initObservers() {
        viewModel.deletePostStatus.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let { result ->
                when(result) {
                    is UiState.Loading -> allPostsProgressBar.visible(true)
                    is UiState.Error -> {
                        allPostsProgressBar.visible(false)
                        snackbar(result.message ?: "Unknown Error")

                    }
                    is UiState.Success -> {
                        allPostsProgressBar.visible(false)
                        postAdapter.posts -= result.data!!
                    }
                }
            }
        })

        viewModel.likePostStatus.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let { result ->
                when(result) {
                    is UiState.Loading -> {
                        currentIndex?.let { index ->
                            postAdapter.posts[index].isLiking = true
                            postAdapter.notifyItemChanged(index)
                        }
                    }
                    is UiState.Error -> {
                        currentIndex?.let { index ->
                            postAdapter.posts[index].isLiking = false
                            postAdapter.notifyItemChanged(index)
                        }
                    }
                    is UiState.Success -> {
                        currentIndex?.let { index ->
                            val uid = auth.uid!!
                            postAdapter.posts[index].apply {
                                this.isLiked = it.peekContent().data!!
                                isLiking = false
                                if(isLiked) {
                                    likedBy += uid
                                }  else {
                                    likedBy -= uid
                                }
                            }
                            postAdapter.notifyItemChanged(index)
                        }
                    }
                }
            }
        })

        viewModel.likedByUsers.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let {result ->
                when(result) {
                    is UiState.Error -> snackbar(result.message ?: "Unknown Error")
                    is UiState.Success -> {
                        val userAdapter = UserAdapter(glide,auth)
                        userAdapter.Users = result.data!!
                        LikedByDialog(userAdapter).show(childFragmentManager,null)
                    }
                }
            }
        })

        viewModel.posts.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let { result ->
                when(result) {
                    is UiState.Loading -> allPostsProgressBar.visible(true)
                    is UiState.Error -> {
                        allPostsProgressBar.visible(false)
                        snackbar(result.message ?: "Unknown Message")
                    }
                    is UiState.Success -> {
                        allPostsProgressBar.visible(false)
                        postAdapter.posts = result.data!!
                    }
                }
            }
        })
    }


}