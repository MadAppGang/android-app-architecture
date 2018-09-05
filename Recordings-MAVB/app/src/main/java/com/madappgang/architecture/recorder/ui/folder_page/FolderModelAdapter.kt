package com.madappgang.architecture.recorder.ui.folder_page

import com.madappgang.architecture.recorder.data.models.FileModel
import com.madappgang.architecture.recorder.managers.FileManager

/**
 * Created by Bohdan Shchavinskiy <bogdan@madappgang.com> on 05.09.2018.
 */
class FolderModelAdapter(var handle: (viewState: FolderViewState) -> Unit = {}) : FolderModelAdapterCallback {

    private val folderModel = FolderModel(this)

    override fun onResult(viewState: FolderViewState) {
        handle(viewState)
    }

    fun onClickToolbarButton() {
        folderModel.onClickToolbarButton()
    }

    fun currentViewState(): FolderViewState = folderModel.currentViewState()

    fun onResume() {
        folderModel.onResume()
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

    fun onClickCreateFolder() {
        folderModel.onClickCreateFolder()
    }

    fun onClickCreateRecord() {
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
}