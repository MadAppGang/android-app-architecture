package com.madappgang.architecture.recorder.ui.folder_page

import android.arch.lifecycle.MutableLiveData
import com.madappgang.architecture.recorder.application.AppInstance
import com.madappgang.architecture.recorder.data.models.FileModel
import com.madappgang.architecture.recorder.managers.FileManager
import com.madappgang.architecture.recorder.managers.ViewStateStoresManager
import com.madappgang.architecture.recorder.ui.player_page.PlayerViewStateStore
import com.madappgang.architecture.recorder.ui.recorder_page.RecorderViewStateStore

/**
 * Created by Bohdan Shchavinskiy <bogdan@madappgang.com> on 05.09.2018.
 */
class FolderModel {

    private val fileManager = AppInstance.managersInstance.fileManager
    private val viewStateStorageManager: ViewStateStoresManager = AppInstance.managersInstance.viewStateStore
    private val folderViewStateStore: FolderViewStateStore? = viewStateStorageManager.folderViewStateStore


    fun changeEditing() {
        folderViewStateStore?.toggleEditing(!currentViewState().editing)
    }

    fun currentViewState(): FolderViewState = folderViewStateStore?.folderView
            ?: FolderViewState()

    fun resume() {
        viewStateStorageManager.resumeFolderActivity()
        AppInstance.managersInstance.playerService.release()
    }

    fun toggleEditing() {
        folderViewStateStore?.toggleEditing(currentViewState().editing)
    }

    fun pushFolder(file: FileModel) {
        folderViewStateStore?.pushFolder(file)
    }

    fun playRecord(file: FileModel) {
        viewStateStorageManager.createPlayerViewStateStore(file)
    }

    fun onClickCreateFolder() {
        folderViewStateStore?.showCreateFolder()
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
    }

    fun showSaveRecording() {
        folderViewStateStore?.showSaveRecording()
    }

    fun popFolder(prevPath: String) {
        folderViewStateStore?.popFolder(FileModel(prevPath))
    }

    fun getViewState(): MutableLiveData<FolderViewState>? {
        return folderViewStateStore?.folderViewState
    }

    fun getPlayerViewStateStore(): MutableLiveData<PlayerViewStateStore>? {
        return viewStateStorageManager.playerViewStateStore
    }

    fun getRecorderViewStateStore(): MutableLiveData<RecorderViewStateStore>? {
        return viewStateStorageManager.recorderViewStateStore
    }
}