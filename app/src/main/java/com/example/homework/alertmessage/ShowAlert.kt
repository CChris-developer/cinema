package com.example.homework.alertmessage

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle

class ShowAlert(private val str: String) : androidx.fragment.app.DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(requireActivity())
            builder.setMessage(str)
                .setPositiveButton("Ok") { _, _ ->
                }

            builder.create()
        } ?: throw IllegalStateException("Произошла ошибка")
    }
}