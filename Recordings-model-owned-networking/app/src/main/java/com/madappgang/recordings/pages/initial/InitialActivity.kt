/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 7/9/18.
 */

package com.madappgang.recordings.pages.initial

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.madappgang.recordings.R
import com.madappgang.recordings.dialogs.EditableDialogFragment
import com.madappgang.recordings.extensions.makeGone
import com.madappgang.recordings.extensions.makeVisible
import com.madappgang.recordings.extensions.showError
import com.madappgang.recordings.models.Foldable
import com.madappgang.recordings.models.Folder
import com.madappgang.recordings.models.validateName
import com.madappgang.recordings.network.NetworkExceptions
import com.madappgang.recordings.network.Result
import com.madappgang.recordings.pages.folder.FolderActivity
import kotlinx.android.synthetic.main.activity_initial.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch

internal class InitialActivity :
    AppCompatActivity(),
    EditableDialogFragment.CompletionHandler,
    EditableDialogFragment.FieldValidationHandler {

    private var getRootFolderJob: Job? = null
    private var createRootFolderJob: Job? = null

    private val uiContext by lazy { UI }
    private val bgContext by lazy { CommonPool }

    private val createRootFolderRequestId = "createRootFolderRequestId"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_initial)

        getRootFolderJob = getRootFolder()
    }

    override fun onDialogPositiveClick(requestId: String, value: String) {
        createRootFolderJob = createRootFolder(value)
    }

    override fun onDialogNegativeClick(requestId: String) {
        onBackPressed()
    }

    override fun onValidField(requestId: String, value: String) = Foldable.validateName(value)

    override fun onDestroy() {
        super.onDestroy()
        getRootFolderJob?.cancel()
        createRootFolderJob?.cancel()
    }

    private fun startFolderActivity(folder: Folder) =
        FolderActivity.start(this@InitialActivity, folder, true)

    private fun showCreateRootFolderDialog(defaultValue: String = "") {
        val configurator = EditableDialogFragment.Configurator().apply {
            requestId = createRootFolderRequestId
            titleTextId = R.string.InitialActivity_Create_root_folder
            hintTextId = R.string.InitialActivity_enter_name
            positiveButtonTextId = R.string.InitialActivity_create
            negativeButtonTextId = R.string.InitialActivity_cancel
            this.defaultValue = defaultValue
        }

        val dialog = EditableDialogFragment.newInstance(configurator)

        dialog.show(supportFragmentManager, "CreateRootFolderDialogTag")
    }

    private fun getRootFolder() = launch(uiContext) {
        progressBar.makeVisible()

        val result = async(bgContext) { Folder.fetchRoot() }.await()

        progressBar.makeGone()

        when (result) {
            is Result.Success -> startFolderActivity(result.value)

            is Result.Failure -> {
                if (result.error is NetworkExceptions.NotFound) {
                    showCreateRootFolderDialog()
                } else {
                    showError(result.error)
                }
            }
        }
    }

    private fun createRootFolder(name: String) = launch(uiContext) {
        val folder = Folder().apply {
            this.name = name
        }
        progressBar.makeVisible()
        val result = async(bgContext) { folder.create() }.await()

        when (result) {
            is Result.Success -> startFolderActivity(result.value)

            is Result.Failure -> {
                showCreateRootFolderDialog(name)
                showError(result.error)
            }
        }

        progressBar.makeGone()
    }

}