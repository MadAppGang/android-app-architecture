/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 7/9/18.
 */

package com.madappgang.recordings.pages.recorder

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.madappgang.recordings.R
import com.madappgang.recordings.applications.app
import com.madappgang.recordings.models.Folder

internal class RecorderActivity : AppCompatActivity() {

    companion object {

        private val FOLDER_KEY = "folder_key"

        fun startForResult(activity: AppCompatActivity, folder: Folder, requestCode: Int) {
            val intent = Intent(activity, RecorderActivity::class.java)
            intent.putExtra(FOLDER_KEY, folder)
            activity.startActivityForResult(intent, requestCode)
        }
    }

    private val recorder by lazy { application.app.dependencyContainer.recorder }

    private val folder by lazy { intent.getParcelableExtra(FOLDER_KEY) as Folder }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recorder)
    }

}