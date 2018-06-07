package com.madappgang.recordings.app.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.madappgang.recordings.R
import com.madappgang.recordings.reckit.FileManager

class InitialActivity : AppCompatActivity() {

    private lateinit var fileManager: FileManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_initial)
    }
}
