package com.madappgang.architecture.recorder.ui.folder_page

import android.arch.lifecycle.MutableLiveData
import com.madappgang.architecture.recorder.data.models.FileModel

/**
 * Created by Bohdan Shchavinskiy <bogdan@madappgang.com> on 05.07.2018.
 */
class FolderViewStateStore {
    var folderView: FolderViewState? = FolderViewState()
    var folderViewState: MutableLiveData<FolderViewState>? = MutableLiveData()

    init {
        folderViewState?.value = folderView
    }

    fun toggleEditing(isEditing: Boolean) {
        folderView = folderView?.copy(action = FolderViewState.Action.TOGGLE_EDITING, editing = isEditing)
        folderViewState?.value = folderView
    }

    fun pushFolder(file: FileModel) {
        folderView = folderView?.copy(action = FolderViewState.Action.PUSH_FOLDER, file = file)
        folderViewState?.value = folderView
    }

    fun popFolder(file: FileModel) {
        folderView = folderView?.copy(action = FolderViewState.Action.POP_FOLDER, file = file)
        folderViewState?.value = folderView
    }

    fun showCreateFolder() {
        folderView = folderView?.copy(action = FolderViewState.Action.SHOW_ALERT, alertType = FolderViewState.AlertType.CREATE_FOLDER)
        folderViewState?.value = folderView
    }

    fun showSaveRecording() {
        folderView = folderView?.copy(action = FolderViewState.Action.SHOW_ALERT, alertType = FolderViewState.AlertType.SAVE_RECORDING)
        folderViewState?.value = folderView
    }

    fun dismissAlert() {
        folderView = folderView?.copy(action = FolderViewState.Action.DISMISS_ALERT, alertType = FolderViewState.AlertType.NO_ALERT)
        folderViewState?.value = folderView
    }

    fun clearData() {
        folderView = null
        folderViewState = null
    }
}