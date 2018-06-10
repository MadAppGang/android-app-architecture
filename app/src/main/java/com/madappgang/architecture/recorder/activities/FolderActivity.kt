package com.madappgang.architecture.recorder.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.madappgang.architecture.recorder.FolderAdapter
import com.madappgang.architecture.recorder.R

class FolderActivity : AppCompatActivity(), FolderAdapter.ItemClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: FolderAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_folder)

        val myDataset = arrayOf("a", "b", "c", "d", "e", "f", "g")

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
        getMenuInflater().inflate(R.menu.documents_menu, menu);
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.create_folder -> {
                onClickCreateFolder()
                return true
            }
            R.id.create_record -> {
                onClickCreateRecord()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onItemClick(title: String) {
        Log.d("TODO Actions",  "Clicked at item: " + title)
    }

    fun onClickCreateFolder() {
        Log.d("TODO Actions", "Create folder")
    }

    fun onClickCreateRecord() {
        Log.d("TODO Actions", "Create record")
    }
}