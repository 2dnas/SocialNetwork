package com.example.socialnetwork.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.socialnetwork.R
import com.example.socialnetwork.adapter.UserAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class LikedByDialog(
    private val userAdapter: UserAdapter
) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val likedByRecycler = RecyclerView(requireContext()).apply{
            adapter = userAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.liked_by_dialog_title)
            .setView(likedByRecycler)
            .create()
    }
}