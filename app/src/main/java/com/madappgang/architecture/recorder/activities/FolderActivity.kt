package com.madappgang.architecture.recorder.activities

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import com.madappgang.architecture.recorder.FolderAdapter
import com.madappgang.architecture.recorder.R
import com.madappgang.architecture.recorder.activities.RecorderActivity.Companion.RECORDER_REQUEST_CODE
import com.madappgang.architecture.recorder.helpers.FileManager
import com.madappgang.architecture.recorder.helpers.FileManager.Companion.mainDirectory
import kotlinx.android.synthetic.main.activity_folder.*
import java.io.File

class FolderActivity : AppCompatActivity(), FolderAdapter.ItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: FolderAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private var toolbarButtonUse: Boolean = false
    private val fileManager = FileManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_folder)
        setSupportActionBar(toolbar)
        toolbarButton.setOnClickListener {
            if (toolbarButtonUse) {
                viewAdapter.setNormalMode()
                toolbarButton.text = getString(R.string.toolbar_button_normal)
            } else {
                viewAdapter.setRemoveMode()
                toolbarButton.text = getString(R.string.toolbar_button_select)
            }
            toolbarButtonUse = !toolbarButtonUse
        }

        viewManager = LinearLayoutManager(this)
        viewAdapter = FolderAdapter(mainDirectory)
        viewAdapter.setupItemClickListener(this)

        recyclerView = findViewById<RecyclerView>(R.id.my_recycler_view).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
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
            viewAdapter.setPathForAdapter(file.absolutePath)
            label.text = file.name
        } else {
            onClickItem(file)
        }
    }

    private fun onClickItem(file: File) {
        PlayerActivity.start(this, file.absolutePath)
    }

    private fun onClickCreateFolder() {
        showNewNameDialog(true)
    }

    private fun onClickCreateRecord() {
        val intent = Intent(this, RecorderActivity::class.java)
        startActivityForResult(intent, RECORDER_REQUEST_CODE)
    }

    private fun onSaveFolder(name: String) {
        fileManager.onSaveFolder(viewAdapter.currentPath, name, object : FileManager.FileManagerCallback {
            override fun onResult() {
                viewAdapter.updateListFiles()
            }
        })
    }

    private fun onSaveRecord(name: String) {
        fileManager.onSaveRecord(viewAdapter.currentPath, name, object : FileManager.FileManagerCallback {
            override fun onResult() {
                viewAdapter.updateListFiles()
            }
        })
    }

    override fun onBackPressed() {
        if (viewAdapter.currentPath != mainDirectory) {
            viewAdapter.setLastPathForAdapter()
            label.text = File(viewAdapter.currentPath).name
        } else {
            super.onBackPressed()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) showNewNameDialog(false)
    }

    private fun showNewNameDialog(isFolderDialog: Boolean) {
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_item_name, null)
        val editName = dialogView.findViewById<EditText>(R.id.editItemName)

        dialogBuilder.setView(dialogView)
        dialogBuilder.setTitle(if (isFolderDialog) R.string.dlg_folder_name_title else R.string.dlg_recording_name_title)
        dialogBuilder.setMessage(if (isFolderDialog) R.string.dlg_folder_name_subtitle else R.string.dlg_recording_name_subtitle)
        dialogBuilder.setPositiveButton(R.string.button_title_save, DialogInterface.OnClickListener { dialog, whichButton ->
            val name = editName.text.toString()
            if (isFolderDialog) onSaveFolder(name) else onSaveRecord(name)
        })
        dialogBuilder.setNegativeButton(R.string.button_title_cancel, DialogInterface.OnClickListener { dialog, whichButton ->
            //pass
        })
        dialogBuilder.create().show()
    }
}
