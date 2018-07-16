/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 7/16/18.
 */

package com.madappgang.recordings.dialogs

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import androidx.core.os.bundleOf
import com.madappgang.recordings.R
import com.madappgang.recordings.extensions.getArgument
import kotlinx.android.synthetic.main.dialog_fragment_editable.view.*

internal class EditableDialogFragment : DialogFragment() {

    interface CompletionHandler {

        fun onDialogPositiveClick(requestId: String, value: String) {
        }

        fun onDialogNegativeClick(requestId: String) {
        }
    }

    interface FieldValidationHandler {

        fun onValidField(requestId: String, value: String): Boolean
    }

    class Configurator {
        var requestId: String = ""
        @StringRes
        var titleTextId: Int = R.string.EditableDialogFragment_title
        @StringRes
        var hintTextId: Int = R.string.EditableDialogFragment_hint
        @StringRes
        var positiveButtonTextId: Int = R.string.EditableDialogFragment_save
        @StringRes
        var negativeButtonTextId: Int = R.string.EditableDialogFragment_cancel
        var defaultValue: String = ""
    }

    companion object {

        private val REQUST_ID = "request_Id"
        private val TITLE_STRING_ID = "title_string_id"
        private val EDIT_TEXT_VALUE_STRING_ID = "edit_text_value_string_id"
        private val EDIT_TEXT_HINT_STRING_ID = "edit_text_hint_string_id"
        private val POSITIVE_BUTTON_STRING_ID = "positive_button_string_id"
        private val NEGATIVE_BUTTON_STRING_ID = "negative_button_string_id"

        fun newInstance(configurator: Configurator): EditableDialogFragment {
            return EditableDialogFragment().apply {
                arguments = bundleOf(
                    REQUST_ID to configurator.requestId,
                    TITLE_STRING_ID to configurator.titleTextId,
                    EDIT_TEXT_HINT_STRING_ID to configurator.hintTextId,
                    POSITIVE_BUTTON_STRING_ID to configurator.positiveButtonTextId,
                    NEGATIVE_BUTTON_STRING_ID to configurator.negativeButtonTextId,
                    EDIT_TEXT_VALUE_STRING_ID to configurator.defaultValue
                )
            }
        }
    }

    private var completionHandler: CompletionHandler? = null
    private var fieldValidationHandler: FieldValidationHandler? = null

    private val requestId by lazy { getArgument(REQUST_ID, "") }
    private val defaultValue by lazy { getArgument(EDIT_TEXT_VALUE_STRING_ID, "") }
    private val titleTextId by lazy { getArgument(TITLE_STRING_ID, 0) }
    private val hintTextId by lazy { getArgument(EDIT_TEXT_HINT_STRING_ID, 0) }
    private val positiveButtonTextId by lazy { getArgument(POSITIVE_BUTTON_STRING_ID, 0) }
    private val negativeButtonTextId by lazy { getArgument(NEGATIVE_BUTTON_STRING_ID, 0) }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val layoutInflater = LayoutInflater.from(requireContext())
        val view = layoutInflater.inflate(R.layout.dialog_fragment_editable, null)

        initDialogTitle(view)
        initNegativeButton(view)
        initPositiveButton(view)
        initEditTextField(view)

        val dialog = createDialog(view)
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        fieldValidationHandler = context as? FieldValidationHandler
        completionHandler = context as? CompletionHandler
    }

    private fun createDialog(view: View?) =
        AlertDialog.Builder(requireContext())
            .setView(view)
            .setOnKeyListener(DialogInterface.OnKeyListener { dialog, keyCode, event ->
                val isBackPressed = keyCode == KeyEvent.KEYCODE_BACK &&
                    event.action == KeyEvent.ACTION_UP
                if (isBackPressed) {
                    completionHandler?.onDialogNegativeClick(requestId)
                    return@OnKeyListener true
                }
                return@OnKeyListener false
            })
            .create()

    private fun initDialogTitle(view: View?) {
        view?.title?.setText(titleTextId)
    }

    private fun initNegativeButton(view: View?) {
        view?.negativeButton?.setText(negativeButtonTextId)
        view?.negativeButton?.setOnClickListener {
            completionHandler?.onDialogNegativeClick(requestId)
            dismiss()
        }
    }

    private fun initPositiveButton(view: View?) {
        view?.positiveButton?.setText(positiveButtonTextId)
        view?.positiveButton?.setOnClickListener {
            val fieldValue = view.field?.text.toString()
            completionHandler?.onDialogPositiveClick(requestId, fieldValue)
            dismiss()
        }
        updatePositiveButton(view)
    }

    private fun initEditTextField(view: View?) {
        view?.field?.setText(defaultValue)
        view?.field?.setHint(hintTextId)
        view?.field?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updatePositiveButton(view)
            }
        })
        updatePositiveButton(view)
    }

    private fun updatePositiveButton(view: View?) {
        val fieldValue = view?.field?.text.toString()
        val isFieldValid = fieldValidationHandler?.onValidField(requestId, fieldValue) ?: true
        view?.positiveButton?.isEnabled = isFieldValid
    }
}