package com.example.homework.movies

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import com.example.homework.R

class ShareDialogFragment(private val str: String) : androidx.fragment.app.DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setMessage(str)
                .setPositiveButton(getString(R.string.share)) { _, _ ->
                }
                .setNegativeButton(getString(R.string.cancel)) { _, _ ->
                }
            builder.create()
        } ?: throw IllegalStateException(getString(R.string.mistake))
    }
}