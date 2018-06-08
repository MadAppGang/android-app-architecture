package com.madappgang.recordings.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.madappgang.recordings.R
import com.madappgang.recordings.core.Folder
import com.madappgang.recordings.kit.FileManager

class FolderActivity : AppCompatActivity() {
    companion object {
        private val FOLDER_EXTRA = "folder_extra"

        fun start(context: Context, folder: Folder) {
            val intent = Intent(context, FolderActivity::class.java)
            intent.putExtra(FOLDER_EXTRA, folder)
            context.startActivity(intent)
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
