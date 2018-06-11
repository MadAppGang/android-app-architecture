package com.madappgang.architecture.recorder.activities

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import com.madappgang.architecture.recorder.FolderAdapter
import com.madappgang.architecture.recorder.R
import com.madappgang.architecture.recorder.activities.RecorderActivity.Companion.RECORDER_REQUEST_CODE
import com.madappgang.architecture.recorder.helpers.Recorder.Companion.mainDirectory
import com.madappgang.architecture.recorder.helpers.Recorder.Companion.recordFormat
import com.madappgang.architecture.recorder.helpers.Recorder.Companion.recordName
import com.madappgang.architecture.recorder.helpers.Recorder.Companion.timeDirectory
import java.io.File

class FolderActivity : AppCompatActivity(), FolderAdapter.ItemClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: FolderAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager

    private lateinit var timeValueName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_folder)

        val myDataset = arrayOf("One", "Two", "Three", "Four", "Five", "Six", "Seven")

        viewManager = LinearLayoutManager(this)
        viewAdapter = FolderAdapter(myDataset)
        viewAdapter.setupItemClickListener(this)

        recyclerView = findViewById<RecyclerView>(R.id.my_recycler_view).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.documents_menu, menu);
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.create_folder -> onClickCreateFolder()
            R.id.create_record -> onClickCreateRecord()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onItemClick(title: String) {
        Log.d("TODO Actions", "Clicked at item: " + title)
        onClickItem(title)
    }

    private fun showNewNameDialog(isFolderDialog: Boolean) {
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_item_name, null)
        dialogBuilder.setView(dialogView)

        val editName = dialogView.findViewById<EditText>(R.id.editItemName)

        dialogBuilder.setTitle(if (isFolderDialog) R.string.dlg_folder_name_title else R.string.dlg_recording_name_title)
        dialogBuilder.setMessage(if (isFolderDialog) R.string.dlg_folder_name_subtitle else R.string.dlg_recording_name_subtitle)
        dialogBuilder.setPositiveButton(R.string.button_title_save, DialogInterface.OnClickListener { dialog, whichButton ->
            val name = editName.text.toString()
            Log.d("TODO Actions", "Entered name : " + name)
            if (isFolderDialog) onSaveFolder(name) else onSaveRecord(name)
        })
        dialogBuilder.setNegativeButton(R.string.button_title_cancel, DialogInterface.OnClickListener { dialog, whichButton ->
            //pass
        })
        val b = dialogBuilder.create()
        b.show()
    }

    private fun onClickItem(title: String) {
        val title = mainDirectory + "/" + recordName + recordFormat
        PlayerActivity.start(this, title)
    }

    private fun onClickCreateFolder() {
        showNewNameDialog(true)
    }

    private fun onClickCreateRecord() {
        val intent = Intent(this, RecorderActivity::class.java)
        startActivityForResult(intent, RECORDER_REQUEST_CODE)
    }

    private fun onSaveFolder(name: String) {
        File(mainDirectory, name).mkdirs()
    }

    private fun onSaveRecord(name: String) {
        timeValueName = name
        val cacheRecord = File(timeDirectory, "$recordName$recordFormat")
        if (cacheRecord.absoluteFile.exists())
            cacheRecord.copyTo(File(mainDirectory, "$name$recordFormat"), true, DEFAULT_BUFFER_SIZE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            showNewNameDialog(false)
        }
    }
}