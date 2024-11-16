package com.example.homework.movies

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.example.homework.R
import com.example.homework.api.Consts.BUNDLE_KEY
import com.example.homework.api.Consts.REQUEST_KEY

class CreateNewCollectionDialogFragment(id: String) : DialogFragment() {

    private lateinit var listener: NoticeDialogListener
    private lateinit var customView: View
    private var collectionName = ""

    interface NoticeDialogListener {
        fun onDialogPositiveClick(str: String)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it, R.style.CustomNewCollectionStyle)
            customView = requireActivity().layoutInflater.inflate(
                R.layout.fragment_create_new_collection_dialog,
                null
            )
            builder.setView(customView)
                .setPositiveButton(
                    R.string.ready
                ) { _, _ ->
                    collectionName =
                        requireView().findViewById<EditText>(R.id.editText).text.toString()
                    listener.onDialogPositiveClick(collectionName)
                    setFragmentResult(REQUEST_KEY, bundleOf(BUNDLE_KEY to collectionName))
                }
            builder.create()
        } ?: throw IllegalStateException(getString(R.string.activity_exception))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return customView
    }
}