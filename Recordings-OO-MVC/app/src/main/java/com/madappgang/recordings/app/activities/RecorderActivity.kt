package com.madappgang.recordings.app.activities

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.madappgang.recordings.R
import com.madappgang.recordings.reccore.Folder
import com.madappgang.recordings.reckit.FileManager
import com.madappgang.recordings.reckit.Recorder

class RecorderActivity : AppCompatActivity() {
    companion object {
        private val FOLDER_EXTRA = "folder_extra"

        fun start(context: Context, folder: Folder) {
            val intent = Intent(context, RecorderActivity::class.java)
            intent.putExtra(FOLDER_EXTRA, folder)
            context.startService(intent)
        }
    }

    private val folder by lazy { intent.getParcelableExtra(FOLDER_EXTRA) as Folder }
//    TODO assign value
    private lateinit var fileManager: FileManager
//    TODO assign value
    private lateinit var recorder: Recorder


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recorder)
    }
}
