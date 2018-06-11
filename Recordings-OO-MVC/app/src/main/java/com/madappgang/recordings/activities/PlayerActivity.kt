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
import com.madappgang.recordings.core.Track
import com.madappgang.recordings.kit.Player

class PlayerActivity : AppCompatActivity() {

    private val track by lazy { intent.getParcelableExtra(TRACK_KEY) as Track }

    //TODO assign value
    private lateinit var player: Player

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
    }

    companion object {

        private val TRACK_KEY = "track_key"

        fun start(context: Context, track: Track) {
            val intent = Intent(context, PlayerActivity::class.java)
            intent.putExtra(TRACK_KEY, track)
            context.startActivity(intent)
        }
    }
}
