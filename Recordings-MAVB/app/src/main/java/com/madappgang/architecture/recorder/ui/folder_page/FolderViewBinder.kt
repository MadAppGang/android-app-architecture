package com.madappgang.architecture.recorder.ui.folder_page

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import com.madappgang.architecture.recorder.R
import com.madappgang.architecture.recorder.application.AppInstance
import com.madappgang.architecture.recorder.data.models.FileModel
import com.madappgang.architecture.recorder.data.storages.FileStorage
import com.madappgang.architecture.recorder.managers.FileManager
import com.madappgang.architecture.recorder.managers.ViewStateStoresManager

/**
 * Created by Bohdan Shchavinskiy <bogdan@madappgang.com> on 01.08.2018.
 */
class FolderViewBinder {

    private var viewAdapter: FolderAdapter? = null
    private val fileManager = AppInstance.managersInstance.fileManager
    private val viewStatStorageManager: ViewStateStoresManager = AppInstance.managersInstance.viewStateStore
    private val folderViewStateStore: FolderViewStateStore? = viewStatStorageManager.folderViewStateStore

    var showDialog: (isFolderDialog: Boolean) -> Unit = {}
    var startPlayer: (filePath: String) -> Unit = {}
    var startRecorder: () -> Unit = {}
    var setTitleText: (text: String) -> Unit = {}
    var setToolbarButtonText: (textId: Int) -> Unit = {}

    private fun restoreState() {
        val file = currentViewState().file ?: FileModel(FileStorage.mainDirectory)
        viewAdapter?.setCurrentPath(file.filePath)
        setTitleText(file.name)
        toggleEditing()
    }

    fun onClickToolbarButton() {
        folderViewStateStore?.toggleEditing(!currentViewState().editing)
    }

    private fun currentViewState(): FolderViewState = folderViewStateStore?.folderView?: FolderViewState()

    fun onResume() {
        viewAdapter?.updateListFiles()
        viewStatStorageManager.resumeFolderActivity()
        AppInstance.managersInstance.playerService.release()
    }

    private fun handle(viewState: FolderViewState) {
        when (viewState.action) {
            FolderViewState.Action.SHOW_ALERT -> {
                if (currentViewState().alertType == FolderViewState.AlertType.CREATE_FOLDER) {
                    showDialog.invoke(true)
                } else if (currentViewState().alertType == FolderViewState.AlertType.SAVE_RECORDING) {
                    showDialog.invoke(false)
                }
            }
            FolderViewState.Action.TOGGLE_EDITING -> {
                toggleEditing()
            }
            FolderViewState.Action.PUSH_FOLDER -> {
                pushFolder()
            }
            FolderViewState.Action.POP_FOLDER -> {
                viewAdapter?.setPathForAdapter(currentViewState().file?.filePath ?: "")
                setTitleText(AppInstance.managersInstance.fileManager.getFileNameByPath(viewAdapter?.getCurrentPath()))
            }
            else -> {
            }
        }
    }

    private fun pushFolder() {
        val file = currentViewState().file ?: FileModel(FileStorage.mainDirectory)
        viewAdapter?.setPathForAdapter(file.filePath)
        setTitleText(file.name)
    }

    private fun toggleEditing() {
        if (currentViewState().editing) {
            viewAdapter?.setRemoveMode()
            setToolbarButtonText(R.string.toolbar_button_select)
        } else {
            viewAdapter?.setNormalMode()
            setToolbarButtonText(R.string.toolbar_button_normal)
        }
    }

    private fun pushFolder(file: FileModel) {
        folderViewStateStore?.pushFolder(file)
    }

    private fun playRecord(file: FileModel) {
        viewStatStorageManager.createPlayerViewStateStore()
        startPlayer(file.filePath)
    }

    fun onClickCreateFolder() {
        folderViewStateStore?.showCreateFolder()
    }

    fun onClickCreateRecord() {
        viewStatStorageManager.createRecorderViewStateStore()
        startRecorder()
    }

    fun onSaveFolder(name: String) {
        fileManager.onSaveFolder(viewAdapter?.getCurrentPath(), name, object : FileManager.FileManagerCallback {
            override fun onResult() {
                viewAdapter?.updateListFiles()
            }
        })
    }

    fun onSaveRecord(name: String) {
        fileManager.onSaveRecord(viewAdapter?.getCurrentPath(), name, object : FileManager.FileManagerCallback {
            override fun onResult() {
                viewAdapter?.updateListFiles()
            }
        })
    }

    fun onDismissAlert() {
        folderViewStateStore?.dismissAlert()
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == AppCompatActivity.RESULT_OK) {
            folderViewStateStore?.showSaveRecording()
        } else {
            AppInstance.managersInstance.recorder.onStopRecord()
        }
    }

    fun onBackPressed(): Boolean = if (viewAdapter?.getCurrentPath() != FileStorage.mainDirectory) {
        val prevPath = viewAdapter?.let { it.prevPath() } ?: ""
        folderViewStateStore?.popFolder(FileModel(prevPath))
        false
    } else true

    fun onItemListClick(file: FileModel) {
        if (file.isDirectory) {
            pushFolder(file)
        } else {
            playRecord(file)
        }
    }
}