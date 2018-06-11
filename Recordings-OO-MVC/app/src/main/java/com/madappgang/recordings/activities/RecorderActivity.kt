/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 6/7/18.
 */

package com.madappgang.recordings.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.madappgang.recordings.R
import com.madappgang.recordings.core.Folder
import com.madappgang.recordings.kit.FileManager
import com.madappgang.recordings.kit.Recorder

class RecorderActivity : AppCompatActivity() {

    private val folder by lazy { intent.getParcelableExtra(FOLDER_KEY) as Folder }

    // TODO assign value
    private lateinit var fileManager: FileManager
    // TODO assign value
    private lateinit var recorder: Recorder


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recorder)
    }

    companion object {

        private val FOLDER_KEY = "folder_key"

        fun startForResult(activity: AppCompatActivity, folder: Folder, requestCode: Int) {
            val intent = Intent(activity, RecorderActivity::class.java)
            intent.putExtra(FOLDER_KEY, folder)
            activity.startActivityForResult(intent, requestCode)
        }
    }
}
