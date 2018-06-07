package com.madappgang.recordings.app.activities

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.madappgang.recordings.R
import com.madappgang.recordings.reccore.Folder
import com.madappgang.recordings.reckit.FileManager

class FolderActivity : AppCompatActivity() {
    companion object {
        private val FOLDER_EXTRA = "folder_extra"

        fun start(context: Context, folder: Folder) {
            val intent = Intent(context, FolderActivity::class.java)
            intent.putExtra(FOLDER_EXTRA, folder)
            context.startService(intent)
        }
    }

    private val folder by lazy { intent.getParcelableExtra(FOLDER_EXTRA) as Folder }
//    TODO assign value
    private lateinit var fileManager: FileManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_folder)
    }
}
