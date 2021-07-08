package com.example.socialnetwork.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.socialnetwork.R
import com.example.socialnetwork.base.BaseFragment
import com.example.socialnetwork.other.UiState
import com.example.socialnetwork.utils.snackbar
import com.example.socialnetwork.utils.visible
import com.example.socialnetwork.viewmodel.AuthViewModel
import kotlinx.android.synthetic.main.fragment_register.*
import javax.inject.Inject


class RegisterFragment : BaseFragment(R.layout.fragment_register) {
    @Inject lateinit var viewModel : AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        injector.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObservers()

        btnRegister.setOnClickListener {
            viewModel.register(
                etEmail.text.toString(),
                etUsername.text.toString(),
                etPassword.text.toString(),
                etRepeatPassword.text.toString()
            )
        }

        tvLogin.setOnClickListener {
            if(findNavController().previousBackStackEntry != null){
                findNavController().popBackStack()
            } else findNavController().navigate(
                RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
            )
        }
    }

    private fun initObservers() {
        viewModel.registerStatus.observe(viewLifecycleOwner ,  {
            it.getContentIfNotHandled()?.let { result ->
                when(result) {
                    is UiState.Success -> {
                        snackbar(getString(R.string.success_registration))
                        registerProgressBar.visible(false)
                    }
                    is UiState.Error -> {
                        registerProgressBar.visible(false)
                        snackbar(result.message ?: "Unknown Error Occurred")
                    }
                    is UiState.Loading -> {
                        registerProgressBar.visible(true)
                    }
                }
            }
        })
    }
}