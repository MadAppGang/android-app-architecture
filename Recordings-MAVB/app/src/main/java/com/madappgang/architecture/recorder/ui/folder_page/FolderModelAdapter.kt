package com.madappgang.architecture.recorder.ui.folder_page

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import com.madappgang.architecture.recorder.data.models.FileModel
import com.madappgang.architecture.recorder.managers.FileManager

/**
 * Created by Bohdan Shchavinskiy <bogdan@madappgang.com> on 05.09.2018.
 */
class FolderModelAdapter(owner: LifecycleOwner, private var folderViewBinderCallback: FolderViewBinderCallback) {

    private val folderModel = FolderModel()

    init {
        folderModel.getViewState()?.observe(owner, Observer {
            it?.let { handle(it) }
        })
        folderModel.getPlayerViewStateStore()?.observe(owner, Observer {
            it?.let { folderViewBinderCallback.onCreatePlayerViewState(it.playFile.filePath) }
                    ?: folderViewBinderCallback.updateListFiles()
        })
        folderModel.getRecorderViewStateStore()?.observe(owner, Observer {
            it?.let { folderViewBinderCallback.onCreateRecorderViewState() }
        })
    }

    private fun handle(viewState: FolderViewState) {
        when (viewState.action) {
            FolderViewState.Action.SHOW_ALERT -> {
                if (viewState.alertType == FolderViewState.AlertType.CREATE_FOLDER) {
                    folderViewBinderCallback.showDialog(true, viewState.editing)
                } else if (viewState.alertType == FolderViewState.AlertType.SAVE_RECORDING) {
                    folderViewBinderCallback.showDialog(false, viewState.editing)
                }
            }
            FolderViewState.Action.PUSH_FOLDER -> {
                folderViewBinderCallback.setFolderTitle(viewState.file.filePath, viewState.file.name)
            }
            FolderViewState.Action.POP_FOLDER -> {
                folderViewBinderCallback.setFolderTitle(viewState.file.filePath, null)
            }
            else -> {
                folderViewBinderCallback.toggleEditing(viewState.editing)
            }
        }
    }

    fun onClickToolbarButton() {
        folderModel.changeEditing()
    }

    fun resume() {
        folderModel.resume()
    }

    fun toggleEditing() {
        folderModel.toggleEditing()
    }

    fun pushFolder(file: FileModel) {
        folderModel.pushFolder(file)
    }

    fun playRecord(file: FileModel) {
        folderModel.playRecord(file)
    }

    fun createFolder() {
        folderModel.onClickCreateFolder()
    }

    fun createRecord() {
        folderModel.onClickCreateRecord()
    }

    fun onSaveFolder(currentPath: String?, name: String, callback: FileManager.FileManagerCallback) {
        folderModel.onSaveFolder(currentPath, name, callback)
    }

    fun onSaveRecord(currentPath: String?, name: String, callback: FileManager.FileManagerCallback) {
        folderModel.onSaveRecord(currentPath, name, callback)
    }

    fun onDismissAlert() {
        folderModel.onDismissAlert()
    }

    fun showSaveRecording() {
        folderModel.showSaveRecording()
    }

    fun popFolder(prevPath: String) {
        folderModel.popFolder(prevPath)
    }

    fun restoreState() {
        val currentViewState = folderModel.currentViewState()
        folderViewBinderCallback.initPage(currentViewState.file)
        handle(currentViewState)
    }
}