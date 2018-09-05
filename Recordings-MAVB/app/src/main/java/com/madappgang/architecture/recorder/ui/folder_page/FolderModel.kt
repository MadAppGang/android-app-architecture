package com.madappgang.architecture.recorder.ui.folder_page

import com.madappgang.architecture.recorder.application.AppInstance
import com.madappgang.architecture.recorder.data.models.FileModel
import com.madappgang.architecture.recorder.managers.FileManager
import com.madappgang.architecture.recorder.managers.ViewStateStoresManager

/**
 * Created by Bohdan Shchavinskiy <bogdan@madappgang.com> on 05.09.2018.
 */
class FolderModel(private val callback: FolderModelAdapterCallback) {

    private val fileManager = AppInstance.managersInstance.fileManager
    private val viewStateStorageManager: ViewStateStoresManager = AppInstance.managersInstance.viewStateStore
    private val folderViewStateStore: FolderViewStateStore? = viewStateStorageManager.folderViewStateStore


    fun onClickToolbarButton() {
        folderViewStateStore?.toggleEditing(!currentViewState().editing)
        callback.onResult(currentViewState())
    }

    fun currentViewState(): FolderViewState = folderViewStateStore?.folderView
            ?: FolderViewState()

    fun onResume() {
        viewStateStorageManager.resumeFolderActivity()
        AppInstance.managersInstance.playerService.release()
    }

    fun toggleEditing() {
        folderViewStateStore?.toggleEditing(currentViewState().editing)
        callback.onResult(currentViewState())
    }

    fun pushFolder(file: FileModel) {
        folderViewStateStore?.pushFolder(file)
        callback.onResult(currentViewState())
    }

    fun playRecord(file: FileModel) {
        viewStateStorageManager.createPlayerViewStateStore()
    }

    fun onClickCreateFolder() {
        folderViewStateStore?.showCreateFolder()
        callback.onResult(currentViewState())
    }

    fun onClickCreateRecord() {
        viewStateStorageManager.createRecorderViewStateStore()
    }

    fun onSaveFolder(currentPath: String?, name: String, callback: FileManager.FileManagerCallback) {
        fileManager.onSaveFolder(currentPath, name, callback)
    }

    fun onSaveRecord(currentPath: String?, name: String, callback: FileManager.FileManagerCallback) {
        fileManager.onSaveRecord(currentPath, name, callback)
    }

    fun onDismissAlert() {
        folderViewStateStore?.dismissAlert()
        callback.onResult(currentViewState())
    }

    fun showSaveRecording() {
        folderViewStateStore?.showSaveRecording()
        callback.onResult(currentViewState())
    }

    fun popFolder(prevPath: String) {
        folderViewStateStore?.popFolder(FileModel(prevPath))
        callback.onResult(currentViewState())
    }
}