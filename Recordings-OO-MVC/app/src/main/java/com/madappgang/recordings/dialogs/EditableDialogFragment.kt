/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 6/10/18.
 */

package com.madappgang.recordings.dialogs

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.AppCompatButton
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.core.os.bundleOf
import com.madappgang.recordings.R
import com.madappgang.recordings.extensions.getArgument

class EditableDialogFragment : DialogFragment() {

    private val requestId by lazy { getArgument(REQUST_ID, "") }
    private val defaultValue by lazy { getArgument(EDIT_TEXT_VALUE_STRING_ID, "") }
    private val titleTextId by lazy { getArgument(TITLE_STRING_ID, 0) }
    private val hintTextId by lazy { getArgument(EDIT_TEXT_HINT_STRING_ID, 0) }
    private val positiveButtonTextId by lazy { getArgument(POSITIVE_BUTTON_STRING_ID, 0) }
    private val negativeButtonTextId by lazy { getArgument(NEGATIVE_BUTTON_STRING_ID, 0) }

    private lateinit var dialogTitle: TextView
    private lateinit var negativeButton: AppCompatButton
    private lateinit var positiveButton: AppCompatButton
    private lateinit var editTextField: EditText

    private var completionHandler: CompletionHandler? = null
    private var fieldValidationHandler: FieldValidationHandler? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val layoutInflater = LayoutInflater.from(requireContext())
        val view = layoutInflater.inflate(R.layout.dialog_fragment_editable, null)

        dialogTitle = view.findViewById(R.id.title)
        negativeButton = view.findViewById(R.id.negativeButton)
        positiveButton = view.findViewById(R.id.positiveButton)
        editTextField = view.findViewById(R.id.field)

        initDialogTitle()
        initNegativeButton()
        initPositiveButton()
        initEditTextField()

        val dialog = createDialog(view)
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        fieldValidationHandler = context as? FieldValidationHandler
        completionHandler = context as? CompletionHandler
    }

    private fun initDialogTitle() {
        dialogTitle.setText(titleTextId)
    }

    private fun initNegativeButton() {
        negativeButton.setText(negativeButtonTextId)
        negativeButton.setOnClickListener {
            completionHandler?.onDialogNegativeClick(requestId)
            dismiss()
        }
    }

    private fun initPositiveButton() {
        positiveButton.setText(positiveButtonTextId)
        positiveButton.setOnClickListener {
            completionHandler?.onDialogPositiveClick(requestId, editTextField.text.toString())
            dismiss()
        }
        updatePositiveButton()
    }

    private fun initEditTextField() {
        editTextField.setText(defaultValue)
        editTextField.setHint(hintTextId)
        editTextField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updatePositiveButton()
            }
        })
        updatePositiveButton()
    }

    private fun updatePositiveButton() {
        val isFieldValid = fieldValidationHandler
                ?.onValidField(requestId, editTextField.text.toString()) ?: true
        positiveButton.isEnabled = isFieldValid
    }

    private fun createDialog(view: View?) =
            AlertDialog.Builder(requireContext())
                    .setView(view)
                    .setOnKeyListener(DialogInterface.OnKeyListener { dialog, keyCode, event ->
                        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                            completionHandler?.onDialogNegativeClick(requestId)
                            return@OnKeyListener true
                        }
                        return@OnKeyListener false
                    })
                    .create()

    interface CompletionHandler {

        fun onDialogPositiveClick(requestId: String, value: String) {
        }

        fun onDialogNegativeClick(requestId: String) {
        }
    }

    interface FieldValidationHandler {

        fun onValidField(requestId: String, value: String): Boolean
    }

    companion object {
        private val REQUST_ID = "request_Id"
        private val TITLE_STRING_ID = "title_string_id"
        private val EDIT_TEXT_VALUE_STRING_ID = "edit_text_value_string_id"
        private val EDIT_TEXT_HINT_STRING_ID = "edit_text_hint_string_id"
        private val POSITIVE_BUTTON_STRING_ID = "positive_button_string_id"
        private val NEGATIVE_BUTTON_STRING_ID = "negative_button_string_id"

        fun newInstance(
                requestId: String,
                @StringRes titleTextId: Int,
                @StringRes hintTextId: Int,
                @StringRes positiveButtonTextId: Int = R.string.EditableDialogFragment_save,
                @StringRes negativeButtonTextId: Int = R.string.EditableDialogFragment_cancel,
                defaultValue: String = ""
        ): EditableDialogFragment {
            return EditableDialogFragment().apply {
                arguments = bundleOf(
                        REQUST_ID to requestId,
                        TITLE_STRING_ID to titleTextId,
                        EDIT_TEXT_HINT_STRING_ID to hintTextId,
                        POSITIVE_BUTTON_STRING_ID to positiveButtonTextId,
                        NEGATIVE_BUTTON_STRING_ID to negativeButtonTextId,
                        EDIT_TEXT_VALUE_STRING_ID to defaultValue
                )
            }
        }
    }
}