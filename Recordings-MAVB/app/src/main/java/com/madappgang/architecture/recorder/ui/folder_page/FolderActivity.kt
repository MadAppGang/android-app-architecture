package com.madappgang.architecture.recorder.ui.folder_page

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import com.madappgang.architecture.recorder.R
import com.madappgang.architecture.recorder.application.AppInstance
import com.madappgang.architecture.recorder.data.models.DialogModel
import com.madappgang.architecture.recorder.ui.player_page.PlayerActivity
import com.madappgang.architecture.recorder.ui.recorder_page.RecorderActivity
import kotlinx.android.synthetic.main.activity_folder.*


class FolderActivity : AppCompatActivity() {

    private var folderViewBinder: FolderViewBinder = AppInstance.managersInstance.folderViewBinder
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
            init()
        }
    }

    private fun init() {
        folderViewBinder.initDelegates(::labelSetText, ::toolbarSetText, ::startRecorderActivity, ::startPlayerActivity,
                ::createDialogBuilder)
        folderViewBinder.init(myRecyclerView, this, LinearLayoutManager(this), this.layoutInflater)
    }

    private fun labelSetText(text: String) {
        label.text = text
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.documents_menu, menu)
        return true
    }

    override fun onResume() {
        super.onResume()
        folderViewBinder.onResume()
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
}
