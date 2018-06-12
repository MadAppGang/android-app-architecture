package com.madappgang.architecture.recorder.activities

import android.Manifest
import android.app.AlertDialog
import android.arch.lifecycle.Observer
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import com.madappgang.architecture.recorder.AppInstance
import com.madappgang.architecture.recorder.FolderAdapter
import com.madappgang.architecture.recorder.R
import com.madappgang.architecture.recorder.activities.RecorderActivity.Companion.RECORDER_REQUEST_CODE
import com.madappgang.architecture.recorder.helpers.FileManager
import com.madappgang.architecture.recorder.helpers.FileManager.Companion.mainDirectory
import com.madappgang.architecture.recorder.view_state_model.FolderViewState
import kotlinx.android.synthetic.main.activity_folder.*
import java.io.File


class FolderActivity : AppCompatActivity(), FolderAdapter.ItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: FolderAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private val fileManager = AppInstance.appInstance.fileManager
    private val viewStateStore = AppInstance.appInstance.viewStateStore
    private val REQUEST_PERMISSION = 200
    private val permissions = arrayOf<String>(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
    private var permissionAccepted = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_folder)
        setSupportActionBar(toolbar)
        toolbarButton.setOnClickListener {
            viewStateStore.toggleEditing(!currentViewState().editing)
        }
        ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSION)

        viewManager = LinearLayoutManager(this)
        viewAdapter = FolderAdapter(mainDirectory)
        viewAdapter.setupItemClickListener(this)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_PERMISSION -> {
                permissionAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
            }
        }
        if (!permissionAccepted) {
            finish()
        } else {
            init()
        }
    }

    private fun init() {
        recyclerView = findViewById<RecyclerView>(R.id.my_recycler_view).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        viewStateStore.folderViewState.observe(this, Observer<FolderViewState> {
            it?.let { handle(it) }
        })

        restoreState()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.documents_menu, menu)
        return true
    }

    override fun onResume() {
        super.onResume()
        viewAdapter.updateListFiles()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.create_folder -> onClickCreateFolder()
            R.id.create_record -> onClickCreateRecord()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onItemClick(file: File) {
        if (file.isDirectory) {
            pushFolder(file)
        } else {
            playRecord(file)
        }
    }

    private fun pushFolder(file: File) {
        viewStateStore.pushFolder(file)
    }

    private fun playRecord(file: File) {
        viewStateStore.setPlaySelection(file)
    }

    private fun onClickCreateFolder() {
        viewStateStore.showCreateFolder()
    }

    private fun onClickCreateRecord() {
        viewStateStore.showRecorder()
    }

    private fun onSaveFolder(name: String) {
        fileManager.onSaveFolder(viewAdapter.getCurrentPath(), name, object : FileManager.FileManagerCallback {
            override fun onResult() {
                viewAdapter.updateListFiles()
            }
        })
    }

    private fun onSaveRecord(name: String) {
        fileManager.onSaveRecord(viewAdapter.getCurrentPath(), name, object : FileManager.FileManagerCallback {
            override fun onResult() {
                viewAdapter.updateListFiles()
            }
        })
    }

    override fun onBackPressed() {
        if (viewAdapter.getCurrentPath() != mainDirectory) {
            val prevPath = viewAdapter.prevPath()
            viewStateStore.popFolder(File(prevPath))
        } else {
            super.onBackPressed()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) viewStateStore.showSaveRecording()
    }

    private fun showNewNameDialog(isFolderDialog: Boolean) {
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_item_name, null)
        val editName = dialogView.findViewById<EditText>(R.id.editItemName)

        dialogBuilder.setView(dialogView)
        dialogBuilder.setTitle(if (isFolderDialog) R.string.dlg_folder_name_title else R.string.dlg_recording_name_title)
        dialogBuilder.setMessage(if (isFolderDialog) R.string.dlg_folder_name_subtitle else R.string.dlg_recording_name_subtitle)
        dialogBuilder.setPositiveButton(R.string.button_title_save, { dialog, whichButton ->
            val name = editName.text.toString()
            if (isFolderDialog) onSaveFolder(name) else onSaveRecord(name)
            viewStateStore.dismissAlert()
        })
        dialogBuilder.setNegativeButton(R.string.button_title_cancel, { dialog, whichButton ->
            viewStateStore.dismissAlert()
        })
        dialogBuilder.setOnCancelListener({
            viewStateStore.dismissAlert()
        })
        dialogBuilder.create().show()
    }

    private fun currentViewState(): FolderViewState = viewStateStore.folderViewState.value!!

    private fun handle(viewState: FolderViewState) {
        when (viewState.action) {
            FolderViewState.Action.SHOW_ALERT -> {
                if (currentViewState().alertType == FolderViewState.AlertType.CREATE_FOLDER) {
                    showNewNameDialog(true)
                } else if (currentViewState().alertType == FolderViewState.AlertType.SAVE_RECORDING) {
                    showNewNameDialog(false)
                }
            }
            FolderViewState.Action.SHOW_RECORD_VIEW -> {
                val intent = Intent(this, RecorderActivity::class.java)
                startActivityForResult(intent, RECORDER_REQUEST_CODE)
            }
            FolderViewState.Action.SHOW_PLAYER_VIEW -> {
                PlayerActivity.start(this, currentViewState().file!!.absolutePath)
            }
            FolderViewState.Action.TOGGLE_EDITING -> {
                toggleEditing()
            }
            FolderViewState.Action.PUSH_FOLDER -> {
                pushFolder()
            }
            FolderViewState.Action.POP_FOLDER -> {
                viewAdapter.setPathForAdapter(currentViewState().file!!.absolutePath)
                label.text = File(viewAdapter.getCurrentPath()).name
            }
            else -> {
            }
        }
    }

    private fun pushFolder() {
        val file = currentViewState().file ?: File(mainDirectory)
        viewAdapter.setPathForAdapter(file.absolutePath)
        label.text = file.name
    }

    private fun toggleEditing() {
        if (currentViewState().editing) {
            viewAdapter.setRemoveMode()
            toolbarButton.text = getString(R.string.toolbar_button_select)
        } else {
            viewAdapter.setNormalMode()
            toolbarButton.text = getString(R.string.toolbar_button_normal)
        }
    }

    private fun restoreState() {
        pushFolder()
        toggleEditing()

        if (currentViewState().alertType == FolderViewState.AlertType.CREATE_FOLDER) {
            showNewNameDialog(true)
        } else if (currentViewState().alertType == FolderViewState.AlertType.SAVE_RECORDING) {
            showNewNameDialog(false)
        }
    }
}
