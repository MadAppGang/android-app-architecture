/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 6/7/18.
 */

package com.madappgang.recordings.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ProgressBar
import com.madappgang.recordings.App
import com.madappgang.recordings.R
import com.madappgang.recordings.core.Folder
import com.madappgang.recordings.core.Id
import com.madappgang.recordings.dialogs.EditableDialogFragment
import com.madappgang.recordings.extensions.makeGone
import com.madappgang.recordings.extensions.makeVisible
import com.madappgang.recordings.extensions.showError
import com.madappgang.recordings.kit.validName
import com.madappgang.recordings.network.NetworkExceptions
import com.madappgang.recordings.network.Result
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch

class InitialActivity :
        AppCompatActivity(),
        EditableDialogFragment.CompletionHandler,
        EditableDialogFragment.FieldValidationHandler {

    private val createRootFolderRequestId = "createRootFolderRequestId"

    private val uiContext by lazy { App.dependencyContainer.uiContext }
    private val bgContext by lazy { App.dependencyContainer.bgContext }
    private val fileManager by lazy { App.dependencyContainer.fileManager }

    private val progressBar by lazy { findViewById<ProgressBar>(R.id.progressBar) }

    private var getRootFolderJob: Job? = null
    private var createRootFolderJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_initial)

        getRootFolderJob = getRootFolder()
    }

    override fun onDestroy() {
        super.onDestroy()
        getRootFolderJob?.cancel()
        createRootFolderJob?.cancel()
    }

    override fun onDialogPositiveClick(requestId: String, value: String) {
        createRootFolderJob = createRootFolder(value)
    }

    override fun onDialogNegativeClick(requestId: String) {
        onBackPressed()
    }

    override fun onValidField(requestId: String, value: String): Boolean {
        return try {
            fileManager.validName(value)
            true
        } catch (e: Throwable) {
            false
        }
    }

    private fun getRootFolder() = launch(uiContext) {
        progressBar.makeVisible()
        val result = async(bgContext) { fileManager.fetch<Folder>(Id()) }.await()
        progressBar.makeGone()
        when (result) {
            is Result.Success -> startFolderActivity(result.value)

            is Result.Failure -> {
                if (result.throwable is NetworkExceptions.FolderIsNotExistException) {
                    showCreateRootFolderDialog()
                } else {
                    showError(result.throwable)
                }
            }
        }
    }

    private fun createRootFolder(name: String) = launch(uiContext) {
        val folder = Folder().apply {
            this.name = name
        }
        progressBar.makeVisible()
        val result = async(bgContext) { fileManager.add(folder) }.await()

        when (result) {
            is Result.Success -> startFolderActivity(result.value)

            is Result.Failure -> {
                showCreateRootFolderDialog(name)
                showError(result.throwable)
            }
        }

        progressBar.makeGone()
    }

    private fun startFolderActivity(folder: Folder) =
            FolderActivity.start(this@InitialActivity, folder, true)

    private fun showCreateRootFolderDialog(defaultValue: String = "") {
        val dialog = EditableDialogFragment.newInstance(
                createRootFolderRequestId,
                R.string.InitialActivity_Create_root_folder,
                R.string.InitialActivity_enter_name,
                R.string.InitialActivity_create,
                R.string.InitialActivity_cancel,
                defaultValue
        )
        dialog.show(supportFragmentManager, "CreateRootFolderDialogTag")
    }
}
