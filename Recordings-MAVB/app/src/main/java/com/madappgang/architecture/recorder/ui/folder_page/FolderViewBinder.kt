package com.madappgang.architecture.recorder.ui.folder_page

import android.arch.lifecycle.LifecycleOwner
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
class FolderViewBinder(owner: LifecycleOwner) : FolderViewBinderCallback {

    var viewAdapter: FolderAdapter? = null
    var folderModelAdapter: FolderModelAdapter = FolderModelAdapter(owner, this)

    var showDialog: (isFolderDialog: Boolean) -> Unit = {}
    var startPlayer: (filePath: String) -> Unit = {}
    var startRecorder: () -> Unit = {}
    var setTitleText: (text: String) -> Unit = {}
    var setToolbarButtonText: (textId: Int) -> Unit = {}
    var initList: (viewAdapter: FolderAdapter?) -> Unit = {}

    fun initState(clickListener: FolderAdapter.ItemClickListener) {
        viewAdapter = FolderAdapter(FileStorage.mainDirectory)
        viewAdapter?.setupItemClickListener(clickListener)
        folderModelAdapter.restoreState()
    }

    override fun initPage(file: FileModel) {
        viewAdapter?.setCurrentPath(file.filePath)
        initList(viewAdapter)
        updateListFiles()
        setTitleText(file.name)
    }

    override fun onCreatePlayerViewState(filePath: String) {
        startPlayer(filePath)
    }

    override fun onCreateRecorderViewState() {
        startRecorder()
    }

    override fun updateListFiles() {
        viewAdapter?.updateListFiles()
    }

    override fun showDialog(isFolderDialog: Boolean, viewStateEditing: Boolean) {
        showDialog(isFolderDialog)
        toggleEditing(viewStateEditing)
    }

    fun onClickToolbarButton() {
        folderModelAdapter.onClickToolbarButton()
    }

    fun onResume() {
        folderModelAdapter.resume()
    }

    private fun pushFolder(file: FileModel) {
        folderModelAdapter.pushFolder(file)
    }

    private fun playRecord(file: FileModel) {
        folderModelAdapter.playRecord(file)
    }

    fun onClickCreateFolder() {
        folderModelAdapter.createFolder()
    }

    fun onClickCreateRecord() {
        folderModelAdapter.createRecord()
    }

    fun dismissAlert() {
        folderModelAdapter.onDismissAlert()
    }

    override fun setFolderTitle(filePath: String, fileName: String?) {
        viewAdapter?.setPathForAdapter(filePath)
        val name = fileName?.let { it }
                ?: AppInstance.managersInstance.fileManager.getFileNameByPath(viewAdapter?.getCurrentPath())
        updateListFiles()
        setTitleText(name)
        folderModelAdapter.toggleEditing()
    }

    override fun toggleEditing(viewStateEditing: Boolean) {
        if (viewStateEditing) {
            viewAdapter?.setRemoveMode()
            setToolbarButtonText(R.string.toolbar_button_select)
        } else {
            viewAdapter?.setNormalMode()
            setToolbarButtonText(R.string.toolbar_button_normal)
        }
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