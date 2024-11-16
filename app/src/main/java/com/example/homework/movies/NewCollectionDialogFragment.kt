package com.example.homework.movies

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
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

class NewCollectionDialogFragment : DialogFragment() {

    private lateinit var customView: View
    var collectionName = ""

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return activity?.let {
            val builder = AlertDialog.Builder(it, R.style.CustomNewCollectionStyle)
            customView = requireActivity().layoutInflater.inflate(
                R.layout.fragment_create_new_collection_dialog,
                null
            )
            builder.setView(customView)
                .setPositiveButton(R.string.ready, object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        collectionName =
                            requireView().findViewById<EditText>(R.id.editText).text.toString()
                        setFragmentResult(REQUEST_KEY, bundleOf(BUNDLE_KEY to collectionName))
                        dismiss()
                    }
                }
                )
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