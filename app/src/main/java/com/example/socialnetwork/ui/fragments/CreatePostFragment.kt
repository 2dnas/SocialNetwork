package com.example.socialnetwork.ui.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.registerForActivityResult
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.example.socialnetwork.R
import com.example.socialnetwork.base.BaseFragment
import com.example.socialnetwork.other.UiState
import com.example.socialnetwork.utils.snackbar
import com.example.socialnetwork.utils.visible
import com.example.socialnetwork.viewmodel.CreatePostViewModel
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.fragment_create_post.*
import javax.inject.Inject

class CreatePostFragment : BaseFragment(R.layout.fragment_create_post) {

    private var currentImageUri : Uri? = null
    private lateinit var croppedImg : ActivityResultLauncher<String>
    @Inject lateinit var glide : RequestManager

    private val cropResultContract = object : ActivityResultContract<String,Uri>(){
        override fun createIntent(context: Context, input: String?): Intent {
            return CropImage.activity()
                .setAspectRatio(16,9)
                .setGuidelines(CropImageView.Guidelines.ON)
                .getIntent(requireContext())
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri {
            val uri = CropImage.getActivityResult(intent).uri
            glide.load(uri).into(ivPostImage)
            setPostImage.visible(false)
            return uri
        }
    }

    @Inject
    lateinit var viewModel: CreatePostViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        injector.inject(this)
        super.onCreate(savedInstanceState)
        croppedImg = registerForActivityResult(cropResultContract) {
            it?.let {
                currentImageUri = it
            }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()

        setPostImage.setOnClickListener {
            croppedImg.launch("image/*")
        }
        ivPostImage.setOnClickListener {
            croppedImg.launch("image/*")
        }
        btnPost.setOnClickListener {
            currentImageUri?.let {
                viewModel.createPost(it,etPostDescription.text.toString())
            } ?: snackbar(getString(R.string.error_no_image_chosen))
        }

    }

    private fun initObservers() {
        viewModel.createPostStatus.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let { result ->
                when (result) {
                    is UiState.Loading -> createPostProgressBar.visible(true)

                    is UiState.Error -> {
                        createPostProgressBar.visible(false)
                        snackbar(result.message ?: "Unknown Error Occurred")
                    }
                    is UiState.Success -> {
                        createPostProgressBar.visible(false)
                        findNavController().popBackStack()
                    }
                }
            }
        })
    }


}