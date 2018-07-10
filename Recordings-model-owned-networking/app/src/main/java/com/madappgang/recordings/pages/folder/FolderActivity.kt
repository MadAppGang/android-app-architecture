/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 7/9/18.
 */

package com.madappgang.recordings.pages.folder

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.madappgang.recordings.R
import com.madappgang.recordings.models.Folder

class FolderActivity : AppCompatActivity() {

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

    private val folder by lazy { intent.getParcelableExtra(FOLDER_KEY) as Folder }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_folder)
    }
}
