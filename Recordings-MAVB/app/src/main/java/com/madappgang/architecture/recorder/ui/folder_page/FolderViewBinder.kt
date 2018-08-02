package com.madappgang.architecture.recorder.ui.folder_page

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.widget.EditText
import com.madappgang.architecture.recorder.R
import com.madappgang.architecture.recorder.application.AppInstance
import com.madappgang.architecture.recorder.data.models.DialogModel
import com.madappgang.architecture.recorder.data.models.FileModel
import com.madappgang.architecture.recorder.data.storages.FileStorage
import com.madappgang.architecture.recorder.managers.FileManager
import com.madappgang.architecture.recorder.managers.ViewStateStoresManager

/**
 * Created by Bohdan Shchavinskiy <bogdan@madappgang.com> on 01.08.2018.
 */
class FolderViewBinder : FolderAdapter.ItemClickListener {

    override fun onItemClick(file: FileModel) {
        if (file.isDirectory) {
            pushFolder(file)
        } else {
            playRecord(file)
        }
    }

    private var viewAdapter: FolderAdapter? = null
    private lateinit var layoutInflater: LayoutInflater
    private val fileManager = AppInstance.managersInstance.fileManager
    private val viewStatStorageManager: ViewStateStoresManager = AppInstance.managersInstance.viewStateStore
    private val folderViewStateStore: FolderViewStateStore? = viewStatStorageManager.folderViewStateStore

    private lateinit var labelSetText: (text: String) -> Unit
    private lateinit var toolbarSetText: (text: Int) -> Unit
    private lateinit var startActivityForResult: () -> Unit
    private lateinit var startPlayerActivity: (filePath: String) -> Unit
    private lateinit var createDialogBuilder: () -> DialogModel

    fun initDelegates(labelSetText: (text: String) -> Unit, toolbarSetText: (textId: Int) -> Unit, startActivityForResult: () -> Unit,
                      startPlayerActivity: (filePath: String) -> Unit, createDialogBuilder: () -> DialogModel) {
        this.labelSetText = labelSetText
        this.toolbarSetText = toolbarSetText
        this.startActivityForResult = startActivityForResult
        this.startPlayerActivity = startPlayerActivity
        this.createDialogBuilder = createDialogBuilder
    }

    fun init(myRecyclerView: RecyclerView, lifecycleOwner: LifecycleOwner, linearLayoutManager: LinearLayoutManager, layoutInflater: LayoutInflater) {
        if (viewAdapter == null) {
            viewAdapter = FolderAdapter(FileStorage.mainDirectory)
            viewAdapter?.setupItemClickListener(this)
        }
        myRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = linearLayoutManager
            adapter = viewAdapter
        }
        this.layoutInflater = layoutInflater

        folderViewStateStore?.folderViewState?.observe(lifecycleOwner, Observer<FolderViewState> {
            it?.let { handle(it) }
        })

        restoreState()
    }

    private fun restoreState() {
        val file = currentViewState().file ?: FileModel(FileStorage.mainDirectory)
        viewAdapter?.setCurrentPath(file.filePath)
        labelSetText(file.name)
        toggleEditing()
    }

    fun onClickToolbarButton() {
        folderViewStateStore?.toggleEditing(!currentViewState().editing)
    }

    private fun currentViewState(): FolderViewState = folderViewStateStore?.folderView
            ?: FolderViewState()


    fun onResume() {
        viewAdapter?.updateListFiles()
        viewStatStorageManager.resumeFolderActivity()
        AppInstance.managersInstance.playerService.release()
    }

    private fun showNewNameDialog(isFolderDialog: Boolean) {
        val dialogModel = createDialogBuilder()
        val dialogBuilder = dialogModel.alertDialog
        val inflater = dialogModel.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_item_name, null)
        val editName = dialogView.findViewById<EditText>(R.id.editItemName)

        dialogBuilder.setView(dialogView)
        dialogBuilder.setTitle(if (isFolderDialog) R.string.dlg_folder_name_title else R.string.dlg_recording_name_title)
        dialogBuilder.setMessage(if (isFolderDialog) R.string.dlg_folder_name_subtitle else R.string.dlg_recording_name_subtitle)
        dialogBuilder.setPositiveButton(R.string.button_title_save, { dialog, whichButton ->
            val name = editName.text.toString()
            if (isFolderDialog) onSaveFolder(name) else onSaveRecord(name)
            folderViewStateStore?.dismissAlert()
        })
        dialogBuilder.setNegativeButton(R.string.button_title_cancel, { dialog, whichButton ->
            folderViewStateStore?.dismissAlert()
        })
        dialogBuilder.setOnCancelListener({
            folderViewStateStore?.dismissAlert()
        })
        dialogBuilder.create().show()
    }

    private fun handle(viewState: FolderViewState) {
        when (viewState.action) {
            FolderViewState.Action.SHOW_ALERT -> {
                if (currentViewState().alertType == FolderViewState.AlertType.CREATE_FOLDER) {
                    showNewNameDialog(true)
                } else if (currentViewState().alertType == FolderViewState.AlertType.SAVE_RECORDING) {
                    showNewNameDialog(false)
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
                labelSetText(AppInstance.managersInstance.fileManager.getFileNameByPath(viewAdapter?.getCurrentPath()))
            }
            else -> {
            }
        }
    }

    private fun pushFolder() {
        val file = currentViewState().file ?: FileModel(FileStorage.mainDirectory)
        viewAdapter?.setPathForAdapter(file.filePath)
        labelSetText(file.name)
    }

    private fun toggleEditing() {
        if (currentViewState().editing) {
            viewAdapter?.setRemoveMode()
            toolbarSetText(R.string.toolbar_button_select)
        } else {
            viewAdapter?.setNormalMode()
            toolbarSetText(R.string.toolbar_button_normal)
        }
    }

    private fun pushFolder(file: FileModel) {
        folderViewStateStore?.pushFolder(file)
    }

    private fun playRecord(file: FileModel) {
        viewStatStorageManager.createPlayerViewStateStore()
        startPlayerActivity(file.filePath)
    }

    fun onClickCreateFolder() {
        folderViewStateStore?.showCreateFolder()
    }

    fun onClickCreateRecord() {
        viewStatStorageManager.createRecorderViewStateStore()
        startActivityForResult()
    }

    private fun onSaveFolder(name: String) {
        fileManager.onSaveFolder(viewAdapter?.getCurrentPath(), name, object : FileManager.FileManagerCallback {
            override fun onResult() {
                viewAdapter?.updateListFiles()
            }
        })
    }

    private fun onSaveRecord(name: String) {
        fileManager.onSaveRecord(viewAdapter?.getCurrentPath(), name, object : FileManager.FileManagerCallback {
            override fun onResult() {
                viewAdapter?.updateListFiles()
            }
        })
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
}