package com.example.socialnetwork.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.socialnetwork.R
import com.example.socialnetwork.base.BaseFragment
import com.example.socialnetwork.other.UiState
import com.example.socialnetwork.ui.activities.MainActivity
import com.example.socialnetwork.utils.snackbar
import com.example.socialnetwork.utils.visible
import com.example.socialnetwork.viewmodel.AuthViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_register.*
import javax.inject.Inject


class LoginFragment : BaseFragment(R.layout.fragment_login) {
    @Inject lateinit var viewModel : AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        injector.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()

        tvRegisterNewAccount.setOnClickListener {
            if(findNavController().previousBackStackEntry != null){
                findNavController().popBackStack()
            }else findNavController().navigate(
                LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
            )
        }

        btnLogin.setOnClickListener {
            viewModel.login(
                etEmailLogin.text.toString(),
                etPasswordLogin.text.toString()
            )
        }
    }


    private fun initObservers() {
        viewModel.loginStatus.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let { result ->
                when(result) {
                    is UiState.Loading -> {
                        loginProgressBar.visible(true)
                    }
                    is UiState.Error ->  {
                        loginProgressBar.visible(false)
                        snackbar(result.message ?: "Unknown Error Occurred")
                    }
                    is UiState.Success -> {
                        loginProgressBar.visible(true)
                        Intent(requireContext(), MainActivity::class.java).also { intent ->
                            startActivity(intent)
                            requireActivity().finish()
                        }
                    }
                }
            }
        })
    }
}