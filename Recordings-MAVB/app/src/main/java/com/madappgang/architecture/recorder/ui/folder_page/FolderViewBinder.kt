package com.madappgang.architecture.recorder.ui.folder_page

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import com.madappgang.architecture.recorder.R
import com.madappgang.architecture.recorder.application.AppInstance
import com.madappgang.architecture.recorder.data.models.FileModel
import com.madappgang.architecture.recorder.data.storages.FileStorage
import com.madappgang.architecture.recorder.managers.FileManager

/**
 * Created by Bohdan Shchavinskiy <bogdan@madappgang.com> on 01.08.2018.
 */
class FolderViewBinder {

    var viewAdapter: FolderAdapter? = null
    var folderModelAdapter: FolderModelAdapter = FolderModelAdapter(::handle)

    var showDialog: (isFolderDialog: Boolean) -> Unit = {}
    var startPlayer: (filePath: String) -> Unit = {}
    var startRecorder: () -> Unit = {}
    var setTitleText: (text: String) -> Unit = {}
    var setToolbarButtonText: (textId: Int) -> Unit = {}

    fun restoreState(clickListener: FolderAdapter.ItemClickListener, initList: (viewAdapter: FolderAdapter?) -> Unit) {
        viewAdapter = FolderAdapter(FileStorage.mainDirectory)
        viewAdapter?.setupItemClickListener(clickListener)
        val currentViewState = folderModelAdapter.currentViewState()
        val file = currentViewState.file
        viewAdapter?.setCurrentPath(file.filePath)
        initList(viewAdapter)
        setTitleText(file.name)
        handle(currentViewState)
    }

    fun onClickToolbarButton() {
        folderModelAdapter.onClickToolbarButton()
    }

    fun onResume() {
        viewAdapter?.updateListFiles()
        folderModelAdapter.onResume()
    }

    private fun handle(viewState: FolderViewState) {
        when (viewState.action) {
            FolderViewState.Action.SHOW_ALERT -> {
                if (viewState.alertType == FolderViewState.AlertType.CREATE_FOLDER) {
                    showDialog(true)
                } else if (viewState.alertType == FolderViewState.AlertType.SAVE_RECORDING) {
                    showDialog(false)
                }
            }
            FolderViewState.Action.PUSH_FOLDER -> {
                pushFolder(viewState)
                folderModelAdapter.toggleEditing()
            }
            FolderViewState.Action.POP_FOLDER -> {
                viewAdapter?.setPathForAdapter(viewState.file.filePath)
                setTitleText(AppInstance.managersInstance.fileManager.getFileNameByPath(viewAdapter?.getCurrentPath()))
                folderModelAdapter.toggleEditing()
            }
            else -> {
            }
        }
        toggleEditing(viewState)
    }

    private fun pushFolder(viewState: FolderViewState) {
        val file = viewState.file
        viewAdapter?.setPathForAdapter(file.filePath)
        setTitleText(file.name)
    }

    private fun toggleEditing(viewState: FolderViewState) {
        if (viewState.editing) {
            viewAdapter?.setRemoveMode()
            setToolbarButtonText(R.string.toolbar_button_select)
        } else {
            viewAdapter?.setNormalMode()
            setToolbarButtonText(R.string.toolbar_button_normal)
        }
    }

    private fun pushFolder(file: FileModel) {
        folderModelAdapter.pushFolder(file)
    }

    private fun playRecord(file: FileModel) {
        folderModelAdapter.playRecord(file)
        startPlayer(file.filePath)
    }

    fun onClickCreateFolder() {
        folderModelAdapter.onClickCreateFolder()
    }

    fun onClickCreateRecord() {
        folderModelAdapter.onClickCreateRecord()
        startRecorder()
    }

    fun onSaveFolder(name: String) {
        folderModelAdapter.onSaveFolder(viewAdapter?.getCurrentPath(), name, object : FileManager.FileManagerCallback {
            override fun onResult() {
                viewAdapter?.updateListFiles()
            }
        })
    }

    fun onSaveRecord(name: String) {
        folderModelAdapter.onSaveRecord(viewAdapter?.getCurrentPath(), name, object : FileManager.FileManagerCallback {
            override fun onResult() {
                viewAdapter?.updateListFiles()
            }
        })
    }

    fun onDismissAlert() {
        folderModelAdapter.onDismissAlert()
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == AppCompatActivity.RESULT_OK) {
            folderModelAdapter.showSaveRecording()
        } else {
            AppInstance.managersInstance.recorder.onStopRecord()
        }
    }

    fun onItemListClick(file: FileModel) {
        if (file.isDirectory) {
            pushFolder(file)
        } else {
            playRecord(file)
        }
    }

    fun onBackPressed(): Boolean = if (viewAdapter?.getCurrentPath() != FileStorage.mainDirectory) {
        val prevPath = viewAdapter?.let { it.prevPath() } ?: ""
        folderModelAdapter.popFolder(prevPath)
        false
    } else true
}