package com.madappgang.recordings.app.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.madappgang.recordings.R
import com.madappgang.recordings.reccore.Track
import com.madappgang.recordings.reckit.Player

class PlayerActivity : AppCompatActivity() {
    companion object {
        private val TRACK_EXTRA = "track_extra"

        fun start(context: Context, track: Track) {
            val intent = Intent(context, PlayerActivity::class.java)
            intent.putExtra(TRACK_EXTRA, track)
            context.startService(intent)
        }
    }

    private val track by lazy { intent.getParcelableExtra(TRACK_EXTRA) as Track }
//    TODO assign value
    private lateinit var player: Player

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
    }
}
