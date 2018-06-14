/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 6/7/18.
 */

package com.madappgang.recordings.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ProgressBar
import com.madappgang.recordings.application.App
import com.madappgang.recordings.R
import com.madappgang.recordings.core.Folder
import com.madappgang.recordings.core.Id
import com.madappgang.recordings.dialogs.EditableDialogFragment
import com.madappgang.recordings.extensions.makeGone
import com.madappgang.recordings.extensions.makeVisible
import com.madappgang.recordings.extensions.showError
import com.madappgang.recordings.kit.validName
import com.madappgang.recordings.core.NetworkExceptions
import com.madappgang.recordings.core.Result
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch

internal class InitialActivity :
        AppCompatActivity(),
        EditableDialogFragment.CompletionHandler,
        EditableDialogFragment.FieldValidationHandler {

    private val progressBar by lazy { findViewById<ProgressBar>(R.id.progressBar) }

    private val fileManager by lazy { App.dependencyContainer.fileManager }

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

    override fun onValidField(requestId: String, value: String): Boolean {
        return try {
            fileManager.validName(value)
            true
        } catch (e: Throwable) {
            false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        getRootFolderJob?.cancel()
        createRootFolderJob?.cancel()
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

    private fun getRootFolder() = launch(uiContext) {
        progressBar.makeVisible()

        val result = async(bgContext) {
            fileManager.fetchEntity(Folder::class.java, Id(""))
        }.await()

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
        val result = async(bgContext) { fileManager.add(folder, Folder::class.java) }.await()

        when (result) {
            is Result.Success -> startFolderActivity(result.value)

            is Result.Failure -> {
                showCreateRootFolderDialog(name)
                showError(result.throwable)
            }
        }

        progressBar.makeGone()
    }
}
