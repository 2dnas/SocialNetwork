package com.example.socialnetwork.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.socialnetwork.R
import com.example.socialnetwork.adapter.UserAdapter
import com.example.socialnetwork.base.BaseFragment
import com.example.socialnetwork.other.UiState
import com.example.socialnetwork.utils.Constants.SEARCH_TIME_DELAY
import com.example.socialnetwork.utils.snackbar
import com.example.socialnetwork.utils.visible
import com.example.socialnetwork.viewmodel.SearchViewModel
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class SearchFragment : BaseFragment(R.layout.fragment_search) {

    @Inject lateinit var userAdapter: UserAdapter

    @Inject lateinit var viewModel : SearchViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        injector.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        initObservers()

        var job : Job? = null
        etSearch.addTextChangedListener { editable ->
            job?.cancel()
            job = lifecycleScope.launch {
                delay(SEARCH_TIME_DELAY)
                editable?.let {
                    viewModel.searchUser(it.toString())
                }
            }
        }

        userAdapter.setOnUserClickListener { user ->
            findNavController()
                .navigate(SearchFragmentDirections.globalActionToOthersProfileFragment(user.uid))

        }
    }


    private fun initObservers() {
        viewModel.searchResult.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let { result ->
                when(result){
                    is UiState.Loading -> searchProgressBar.visible(true)
                    is UiState.Error -> {
                        searchProgressBar.visible(false)
                        snackbar(result.message ?: "Unknown Error")
                    }
                    is UiState.Success -> {
                        searchProgressBar.visible(false)
                        userAdapter.Users = result.data!!
                    }
                }
            }
        })

    }

    private fun setupRecyclerView() {
        rvSearchResults.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = userAdapter

        }
    }
}