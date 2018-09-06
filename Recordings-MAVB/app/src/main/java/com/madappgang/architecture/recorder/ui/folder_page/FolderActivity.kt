package com.madappgang.architecture.recorder.ui.folder_page

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import com.madappgang.architecture.recorder.R
import com.madappgang.architecture.recorder.data.models.DialogModel
import com.madappgang.architecture.recorder.data.models.FileModel
import com.madappgang.architecture.recorder.ui.player_page.PlayerActivity
import com.madappgang.architecture.recorder.ui.recorder_page.RecorderActivity
import kotlinx.android.synthetic.main.activity_folder.*

class FolderActivity : AppCompatActivity(), FolderAdapter.ItemClickListener {

    private var folderViewBinder: FolderViewBinder = FolderViewBinder(this)
    private val REQUEST_PERMISSION = 200
    private val permissions = arrayOf<String>(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO)
    private var permissionAccepted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_folder)
        setSupportActionBar(toolbar)
        toolbarButton.setOnClickListener {
            folderViewBinder.onClickToolbarButton()
        }
        ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSION)
    }

    override fun onResume() {
        super.onResume()
        folderViewBinder.onResume()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_PERMISSION -> {
                if (grantResults.isNotEmpty()) {
                    permissionAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                }
            }
        }
        if (!permissionAccepted) {
            finish()
        } else {
            prepareViewBinder()
            folderViewBinder.initState(this)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.documents_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.create_folder -> folderViewBinder.onClickCreateFolder()
            R.id.create_record -> folderViewBinder.onClickCreateRecord()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (folderViewBinder.onBackPressed()) super.onBackPressed()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        folderViewBinder.onActivityResult(requestCode, resultCode, data)
    }

    private fun prepareViewBinder() {
        folderViewBinder.showDialog = { isFolderDialog -> showNewNameDialog(isFolderDialog) }
        folderViewBinder.startPlayer = { filePath -> startPlayerActivity(filePath) }
        folderViewBinder.startRecorder = { startRecorderActivity() }
        folderViewBinder.setTitleText = { titleText -> labelSetText(titleText) }
        folderViewBinder.setToolbarButtonText = { toolbarButtonTextId -> toolbarSetText(toolbarButtonTextId) }
        folderViewBinder.initList = { viewAdapter -> initList(viewAdapter) }
    }

    private fun labelSetText(text: String) {
        val title = if (TextUtils.isEmpty(text)) resources.getString(R.string.app_name) else text
        label.text = title
    }

    private fun toolbarSetText(textId: Int) {
        toolbarButton.text = getString(textId)
    }

    private fun startRecorderActivity() {
        val intent = Intent(this, RecorderActivity::class.java)
        startActivityForResult(intent, RecorderActivity.RECORDER_REQUEST_CODE)
    }

    private fun startPlayerActivity(filePath: String) {
        PlayerActivity.start(this, filePath)
    }

    private fun createDialogBuilder(): DialogModel {
        return DialogModel(AlertDialog.Builder(this), this.layoutInflater)
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
        dialogBuilder.setPositiveButton(R.string.button_title_save, { _, _ ->
            val name = editName.text.toString()
            if (isFolderDialog) folderViewBinder.onSaveFolder(name) else folderViewBinder.onSaveRecord(name)
            folderViewBinder.dismissAlert()
        })
        dialogBuilder.setNegativeButton(R.string.button_title_cancel, { _, _ ->
            folderViewBinder.dismissAlert()
        })
        dialogBuilder.setOnCancelListener({
            folderViewBinder.dismissAlert()
        })
        dialogBuilder.create().show()
    }

    private fun initList(viewAdapter: FolderAdapter?) {
        myRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = viewAdapter
        }
    }

    override fun onItemClick(file: FileModel) {
        folderViewBinder.onItemListClick(file)
    }
}
