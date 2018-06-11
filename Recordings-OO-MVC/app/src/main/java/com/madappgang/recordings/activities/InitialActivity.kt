/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 6/7/18.
 */

package com.madappgang.recordings.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.madappgang.recordings.R
import com.madappgang.recordings.kit.FileManager

class InitialActivity : AppCompatActivity() {

    private lateinit var fileManager: FileManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_initial)
    }
}
