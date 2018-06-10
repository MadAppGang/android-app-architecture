/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 6/7/18.
 */

package com.madappgang.recordings.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.madappgang.recordings.R
import com.madappgang.recordings.core.Folder
import com.madappgang.recordings.kit.FileManager

class FolderActivity : AppCompatActivity() {

    private val folder by lazy { intent.getParcelableExtra(FOLDER_KEY) as Folder }

    // TODO assign value
    private lateinit var fileManager: FileManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_folder)
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
