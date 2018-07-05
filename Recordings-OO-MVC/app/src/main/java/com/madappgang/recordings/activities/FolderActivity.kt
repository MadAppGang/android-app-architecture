/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 6/7/18.
 */

package com.madappgang.recordings.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import com.madappgang.recordings.R
import com.madappgang.recordings.adapters.FoldableAdapter
import com.madappgang.recordings.application.App
import com.madappgang.recordings.core.Foldable
import com.madappgang.recordings.core.Folder
import com.madappgang.recordings.core.Result
import com.madappgang.recordings.dialogs.EditableDialogFragment
import com.madappgang.recordings.extensions.showError
import kotlinx.android.synthetic.main.activity_folder.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch

internal class FolderActivity :
    AppCompatActivity(),
    EditableDialogFragment.CompletionHandler,
    EditableDialogFragment.FieldValidationHandler {

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

    private val fileManager by lazy { App.dependencyContainer.fileManager }
    private val toolbar by lazy { findViewById<Toolbar>(R.id.toolbar) }

    private var isFloatingMenuOpen = false
    private val adapter = FoldableAdapter()

    private var loadFolderContentJob: Job? = null
    private var removeFoldableJob: Job? = null

    private val uiContext by lazy { UI }
    private val bgContext by lazy { CommonPool }

    private val createFolderRequestId = "createFolderRequestId"
    private val recorderActivityRequestId = 6731

    private val folder by lazy { intent.getParcelableExtra(FOLDER_KEY) as Folder }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_folder)
        initToolbar()
        initFloatingMenu()
        initList()
        loadFolderContentJob = loadFolderContent()
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

    override fun onDialogPositiveClick(requestId: String, value: String) {
        createFolder(value)
    }

    override fun onValidField(requestId: String, value: String) = fileManager.validateName(value)

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val isTrackRecorded = requestCode == recorderActivityRequestId && resultCode == RESULT_OK
        if (isTrackRecorded) {
            loadFolderContentJob?.cancel()
            loadFolderContentJob = loadFolderContent()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        loadFolderContentJob?.cancel()
        removeFoldableJob?.cancel()
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
            createFolder.visibility = View.GONE
            recordTrack.visibility = View.GONE
            ContextCompat.getDrawable(this, R.drawable.ic_add_white_24dp)

        } else {
            createFolder.visibility = View.VISIBLE
            recordTrack.visibility = View.VISIBLE
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
                adapter.remove(index)
                removeFoldableJob = removeFoldable(foldable)
            }
        }
        itemsList.layoutManager = LinearLayoutManager(this)
        itemsList.adapter = adapter

        swipeRefreshLayout.setOnRefreshListener {
            loadFolderContentJob?.cancel()
            loadFolderContentJob = loadFolderContent()
        }
    }

    private fun showCreateFolderDialog(defaultValue: String = "") {
        val configurator = EditableDialogFragment.Configurator().apply {
            requestId = createFolderRequestId
            titleTextId = R.string.InitialActivity_Create_root_folder
            hintTextId = R.string.InitialActivity_enter_name
            positiveButtonTextId = R.string.InitialActivity_create
            negativeButtonTextId = R.string.InitialActivity_cancel
            this.defaultValue = defaultValue
        }

        val dialog = EditableDialogFragment.newInstance(configurator)

        dialog.show(supportFragmentManager, "CreateFolderDialogTag")
    }

    private fun removeFoldable(foldable: Foldable) = launch(uiContext) {
        val result = async(bgContext) { fileManager.remove(foldable) }.await()
        when (result) {
            is Result.Failure<*> -> {
                showError(result.throwable)
            }
        }
    }

    private fun loadFolderContent() = launch(uiContext) {
        swipeRefreshLayout.isRefreshing = true

        val result = async(bgContext) { fileManager.fetchList(folder) }.await()

        when (result) {
            is Result.Success -> {
                adapter.set(result.value)
            }
            is Result.Failure -> {
                showError(result.throwable)
            }
        }
        swipeRefreshLayout.isRefreshing = false
    }

    private fun createFolder(name: String) = launch(uiContext) {
        swipeRefreshLayout.isRefreshing = true

        val folder = Folder().apply {
            this.path = folder.getFullPath()
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
        swipeRefreshLayout.isRefreshing = false
    }
}
