package com.example.socialnetwork.ui.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.widget.addTextChangedListener
import com.bumptech.glide.RequestManager
import com.example.socialnetwork.R
import com.example.socialnetwork.base.BaseFragment
import com.example.socialnetwork.data.model.ProfileUpdate
import com.example.socialnetwork.other.UiState
import com.example.socialnetwork.utils.snackbar
import com.example.socialnetwork.utils.visible
import com.example.socialnetwork.viewmodel.SettingsViewModel
import com.google.firebase.auth.FirebaseAuth
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.fragment_settings.*
import javax.inject.Inject

class SettingsFragment : BaseFragment(R.layout.fragment_settings) {

    @Inject lateinit var glide : RequestManager
    @Inject lateinit var viewModel : SettingsViewModel
    @Inject lateinit var auth : FirebaseAuth

    private var currentImageUri : Uri? = null

    private lateinit var cropImage : ActivityResultLauncher<Any?>

    private val cropActivityResultContract = object : ActivityResultContract<Any?,Uri> () {
        override fun createIntent(context: Context, input: Any?): Intent {
            return CropImage.activity()
                .setAspectRatio(1,1)
                .setGuidelines(CropImageView.Guidelines.ON)
                .getIntent(requireContext())
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            currentImageUri = CropImage.getActivityResult(intent)?.uri
            return currentImageUri
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        injector.inject(this)
        super.onCreate(savedInstanceState)

        cropImage = registerForActivityResult(cropActivityResultContract) { uri->
            uri?.let {
                viewModel.setCurrentImage(it)
                btnUpdateProfile.isEnabled = true
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
        val uid = auth.uid!!
        viewModel.getUser(uid)
        btnUpdateProfile.isEnabled = false
        etUsernameProfile.addTextChangedListener {
            btnUpdateProfile.isEnabled = true
        }
        etDescriptionProfile.addTextChangedListener {
            btnUpdateProfile.isEnabled = true
        }
        ivProfileImageProfile.setOnClickListener {
            cropImage.launch(null)
        }
        btnUpdateProfile.setOnClickListener {
            val username = etUsernameProfile.text.toString()
            val description = etDescriptionProfile.text.toString()
            val profileUpdate = ProfileUpdate(uid,username,description,currentImageUri)
            viewModel.updateProfile(profileUpdate)
        }
    }


    private fun initObservers() {
        viewModel.getUserStatus.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled().let {result ->
                when(result){
                    is UiState.Error -> {
                        settingsProgressBar.visible(false)
                        snackbar(result.message ?: "Unknown message")
                    }
                    is UiState.Loading -> settingsProgressBar.visible(true)
                    is UiState.Success -> {
                        settingsProgressBar.visible(false)
                        glide.load(result.data?.profilePictureUrl).into(ivProfileImageProfile)
                        etUsernameProfile.setText(result.data?.username)
                        etDescriptionProfile.setText(result.data?.descriptor)
                        btnUpdateProfile.isEnabled = false
                    }
                }
            }
        })

        viewModel.currentImageUri.observe(viewLifecycleOwner, {
            currentImageUri = it
            glide.load(it).into(ivProfileImageProfile)
        })

        viewModel.updateProfileStatus.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let { result ->
                when(result) {
                    is UiState.Loading -> {
                        settingsProgressBar.visible(true)
                        btnUpdateProfile.isEnabled = false
                    }
                    is UiState.Error -> {
                        settingsProgressBar.visible(false)
                        snackbar(result.message ?: "Unknown Error")
                        btnUpdateProfile.isEnabled = true
                    }
                    is UiState.Success -> {
                        settingsProgressBar.visible(false)
                        btnUpdateProfile.isEnabled = false
                        snackbar(requireContext().getString(R.string.profile_updated))
                    }
                }
            }
        })




    }
}