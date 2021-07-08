package com.example.socialnetwork.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.socialnetwork.R
import com.example.socialnetwork.adapter.CommentAdapter
import com.example.socialnetwork.base.BaseActivity
import com.example.socialnetwork.base.BaseDialogFragment
import com.example.socialnetwork.other.UiState
import com.example.socialnetwork.utils.snackbar
import com.example.socialnetwork.utils.visible
import com.example.socialnetwork.viewmodel.CommentViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class CommentDialog : BaseDialogFragment() {

    @Inject lateinit var glide : RequestManager
    @Inject lateinit var commentsAdapter : CommentAdapter
    @Inject lateinit var commentViewModel : CommentViewModel
    @Inject lateinit var auth : FirebaseAuth

    private var postId : String? = null

    private lateinit var dialogView : View


    private lateinit var rvComment : RecyclerView
    private lateinit var etComment : EditText
    private lateinit var btnComment : Button
    private lateinit var commentProgress : ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        injector.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return dialogView
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogView = LayoutInflater.from(requireContext()).inflate(
            R.layout.fragment_comment,
            null
        )
        rvComment = dialogView.findViewById(R.id.rvComments)
        etComment = dialogView.findViewById(R.id.etComment)
        btnComment = dialogView.findViewById(R.id.btnComment)
        commentProgress = dialogView.findViewById(R.id.commentProgressBar)

        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.comments)
            .setView(dialogView)
            .create()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        initObservers()
        postId = arguments?.getString("postId")
        commentViewModel.getCommentsForPost(postId!!)
        btnComment.setOnClickListener {
            val commentText = etComment.text.toString()
            commentViewModel.createComment(commentText,postId!!)
            etComment.text.clear()
        }

        commentsAdapter.setOnDeleteCommentClickListener { comment ->
            commentViewModel.deleteComment(comment)
        }
        commentsAdapter.setOnUserClickListener { comment ->
            if(auth.uid!! == comment.uid) {
                requireActivity().bottomNavigationView.selectedItemId = R.id.profileFragment
                return@setOnUserClickListener
            }
            findNavController().navigate(
                CommentDialogDirections.globalActionToOthersProfileFragment(comment.uid)
            )
        }
    }

    private fun initObservers() {
        commentViewModel.commentForPosts.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let { result ->
                when(result) {
                    is UiState.Loading -> commentProgress.visible(true)
                    is UiState.Error -> {
                        commentProgress.visible(false)
                        snackbar(result.message ?: "Unknown Error")
                    }
                    is UiState.Success -> {
                        commentProgress.visible(false)
                        commentsAdapter.comments = result.data!!
                    }
                }
            }
        })

        commentViewModel.createCommentStatus.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let { result ->
                when(result) {
                    is UiState.Loading -> {
                        commentProgress.visible(true)
                        btnComment.isEnabled = false
                    }
                    is UiState.Error -> {
                        commentProgress.visible(false)
                        snackbar(result.message ?: "Unknown Error")
                        btnComment.isEnabled = true

                    }
                    is UiState.Success -> {
                        commentProgress.visible(false)
                        commentsAdapter.comments += result.data!!
                        btnComment.isEnabled = true
                    }
                }
            }
        })

        commentViewModel.deleteCommentStatus.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let { result ->
                when(result) {
                    is UiState.Loading -> commentProgress.visible(true)
                    is UiState.Error -> {
                        commentProgress.visible(false)
                        snackbar(result.message ?: "Unknown Error")

                    }
                    is UiState.Success -> {
                        commentProgress.visible(false)
                        commentsAdapter.comments -= result.data!!
                    }
                }
            }
        })
    }

    private fun setupRecyclerView() {
        rvComment.apply {
            adapter = commentsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }
}