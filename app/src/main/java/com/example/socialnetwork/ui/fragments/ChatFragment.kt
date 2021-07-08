package com.example.socialnetwork.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.socialnetwork.R
import com.example.socialnetwork.adapter.ChatListAdapter
import com.example.socialnetwork.base.BaseFragment
import com.example.socialnetwork.other.UiState
import com.example.socialnetwork.utils.snackbar
import com.example.socialnetwork.utils.visible
import com.example.socialnetwork.viewmodel.ChatViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class ChatFragment : BaseFragment(R.layout.fragment_chat) {

    @Inject lateinit var viewModel : ChatViewModel
    @Inject lateinit var chatAdapter : ChatListAdapter
    @Inject lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        injector.inject(this)
        super.onCreate(savedInstanceState)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
        setupRecyclerView()
        val receiverUid = arguments?.getString("uid")
        receiverUid.let {
            viewModel.getMessages(it!!)
        }

        ivSendMessage.setOnClickListener {
            val message = etChatFragment.text.toString()
            if (receiverUid != null) {
                if(etChatFragment.text?.isEmpty()!!) return@setOnClickListener
                viewModel.addChatMessage(message,receiverUid)
                etChatFragment.setText("")
            }
        }
    }



    private fun initObservers() {
        viewModel.messages.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let { result ->
                when(result) {
                    is UiState.Loading -> chatFragmentProgressBar.visible(true)
                    is UiState.Error -> {
                        snackbar(result.message ?: "Unknown Error")
                        chatFragmentProgressBar.visible(false)
                    }
                    is UiState.Success -> {
                        chatFragmentProgressBar.visible(false)
                        chatAdapter.messages = result.data!!
//                        chatAdapter.notifyDataSetChanged()
                    }
                }
            }
        })
    }

    private fun setupRecyclerView() {
        rvChatFragment.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = chatAdapter
        }
    }
}