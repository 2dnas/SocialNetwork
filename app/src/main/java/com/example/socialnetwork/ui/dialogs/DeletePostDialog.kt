package com.example.socialnetwork.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.socialnetwork.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class DeletePostDialog : DialogFragment() {

    private var positiveListener : (() -> Unit)? = null

    fun setPositiveButtonListener(listener : () -> Unit) {
        positiveListener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.delete_post_dialog_title))
            .setMessage(getString(R.string.delete_post_dialog_message))
            .setIcon(R.drawable.ic_delete)
            .setPositiveButton(R.string.delete_post_dialog_positive) { _ ,_ ->
                positiveListener?.invoke()
            }
            .setNegativeButton(R.string.delete_post_dialog_negative) { dialog, _ ->
                dialog.cancel()
            }
            .create()

    }
}