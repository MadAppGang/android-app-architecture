/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 7/9/18.
 */

package com.madappgang.recordings.pages.player

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.madappgang.recordings.R
import com.madappgang.recordings.applications.app
import com.madappgang.recordings.models.Track

class PlayerActivity : AppCompatActivity() {

    companion object {

        private val TRACK_KEY = "track_key"

        fun start(context: Context, track: Track) {
            val intent = Intent(context, PlayerActivity::class.java)
            intent.putExtra(TRACK_KEY, track)
            context.startActivity(intent)
        }
    }

    private val player by lazy { application.app.dependencyContainer.player }

    private val track by lazy { intent.getParcelableExtra(TRACK_KEY) as Track }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
    }
}
