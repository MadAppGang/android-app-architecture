/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 6/7/18.
 */

package com.madappgang.recordings.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.widget.TextView
import com.madappgang.recordings.R
import com.madappgang.recordings.adapters.FoldableAdapter
import com.madappgang.recordings.application.App
import com.madappgang.recordings.core.Foldable
import com.madappgang.recordings.core.Folder
import com.madappgang.recordings.dialogs.EditableDialogFragment
import com.madappgang.recordings.extensions.makeGone
import com.madappgang.recordings.extensions.makeVisible
import com.madappgang.recordings.extensions.showError
import com.madappgang.recordings.kit.validName
import com.madappgang.recordings.network.Result
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch

class FolderActivity :
        AppCompatActivity(),
        EditableDialogFragment.CompletionHandler,
        EditableDialogFragment.FieldValidationHandler {

    private val createFolderRequestId = "createFolderRequestId"
    private val recorderActivityRequestId = 6731

    private val folder by lazy { intent.getParcelableExtra(FOLDER_KEY) as Folder }

    private val uiContext by lazy { App.dependencyContainer.uiContext }
    private val bgContext by lazy { App.dependencyContainer.bgContext }
    private val fileManager by lazy { App.dependencyContainer.fileManager }

    private val toolbar by lazy { findViewById<Toolbar>(R.id.toolbar) }
    private val toolbarTitle by lazy { findViewById<TextView>(R.id.toolbarTitle) }
    private val swipeRefresherView by lazy { findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayout) }
    private val foldableListView by lazy { findViewById<RecyclerView>(R.id.itemsList) }
    private val recordTrack by lazy { findViewById<FloatingActionButton>(R.id.recordTrack) }
    private val createFolder by lazy { findViewById<FloatingActionButton>(R.id.createFolder) }
    private val floatingMenu by lazy { findViewById<FloatingActionButton>(R.id.floatingMenu) }

    private var isFloatingMenuOpen = false
    private val adapter = FoldableAdapter()

    private var loadFolderContentJob: Job? = null
    private var removeFoldableJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_folder)
        initToolbar()
        initFloatingMenu()
        initList()
        loadFolderContentJob = loadFolderContent()
    }

    private fun initToolbar() {
        toolbarTitle.text = folder.name
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        if (!isTaskRoot) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun initFloatingMenu() {
        floatingMenu.setOnClickListener {
            updateFloatingMenu()
            isFloatingMenuOpen = !isFloatingMenuOpen
        }

        createFolder.setOnClickListener {
            showCreateFolderDialog()
            updateFloatingMenu()
        }

        recordTrack.setOnClickListener {
            RecorderActivity.startForResult(this, folder, recorderActivityRequestId)
            updateFloatingMenu()
        }
    }

    private fun updateFloatingMenu() {
        val floatingMenuIcon = if (isFloatingMenuOpen) {
            createFolder.makeGone()
            recordTrack.makeGone()
            ContextCompat.getDrawable(this, R.drawable.ic_add_white_24dp)

        } else {
            createFolder.makeVisible()
            recordTrack.makeVisible()
            ContextCompat.getDrawable(this, R.drawable.ic_close_white_24dp)
        }
        floatingMenu.setImageDrawable(floatingMenuIcon)
    }

    private fun initList() {
        adapter.apply {
            onFolderItemClicked = {
                FolderActivity.start(this@FolderActivity, it)
            }

            onTrackItemClicked = {
                PlayerActivity.start(this@FolderActivity, it)
            }

            onRemoveItemClicked = { foldable, index ->
                adapter.removeAt(index)
                removeFoldableJob = removeFoldable(foldable)
            }
        }
        foldableListView.layoutManager = LinearLayoutManager(this)
        foldableListView.adapter = adapter

        swipeRefresherView.setOnRefreshListener {
            loadFolderContentJob?.cancel()
            loadFolderContentJob = loadFolderContent()
        }
    }

    private fun removeFoldable(foldable: Foldable) = launch(uiContext) {
        val result = async(bgContext) { fileManager.remove(foldable) }.await()
        when (result) {
            is Result.Failure -> {
                showError(result.throwable)
            }
        }
    }

    private fun loadFolderContent() = launch(uiContext) {
        swipeRefresherView.isRefreshing = true

        val result = async(bgContext) { fileManager.fetchList(folder) }.await()

        when (result) {
            is Result.Success -> {
                adapter.setData(result.value)
            }
            is Result.Failure -> {
                showError(result.throwable)
            }
        }
        swipeRefresherView.isRefreshing = false
    }

    private fun showCreateFolderDialog(defaultValue: String = "") {
        val dialog = EditableDialogFragment.newInstance(
                createFolderRequestId,
                R.string.InitialActivity_Create_root_folder,
                R.string.InitialActivity_enter_name,
                R.string.InitialActivity_create,
                R.string.InitialActivity_cancel,
                defaultValue
        )
        dialog.show(supportFragmentManager, "CreateFolderDialogTag")
    }

    override fun onDialogPositiveClick(requestId: String, value: String) {
        createFolder(value)
    }

    override fun onValidField(requestId: String, value: String) = try {
        fileManager.validName(value)
        true
    } catch (e: Throwable) {
        false
    }

    private fun createFolder(name: String) = launch(uiContext) {
        swipeRefresherView.isRefreshing = true

        val folder = Folder().apply {
            this.folderId = folder.id
            this.name = name
        }

        val result = async(bgContext) { fileManager.add(folder) }.await()

        when (result) {
            is Result.Success -> {
                loadFolderContentJob?.cancel()
                loadFolderContentJob = loadFolderContent()
            }
            is Result.Failure -> {
                showCreateFolderDialog(name)
                showError(result.throwable)
            }
        }
        swipeRefresherView.isRefreshing = false
    }

    override fun onDestroy() {
        super.onDestroy()
        loadFolderContentJob?.cancel()
        removeFoldableJob?.cancel()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == recorderActivityRequestId && resultCode == RESULT_OK) {
            loadFolderContentJob?.cancel()
            loadFolderContentJob = loadFolderContent()
        }
    }

    companion object {

        private val FOLDER_KEY = "folder_key"

        fun start(context: Context, folder: Folder, asRoot: Boolean = false) {
            val intent = Intent(context, FolderActivity::class.java)
            intent.putExtra(FOLDER_KEY, folder)
            if (asRoot) {
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            context.startActivity(intent)
        }
    }
}
