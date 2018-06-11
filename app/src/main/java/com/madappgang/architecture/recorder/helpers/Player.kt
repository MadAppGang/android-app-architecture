package com.madappgang.architecture.recorder.helpers

import android.media.MediaPlayer
import android.util.Log
import java.io.IOException

class Player(private val fileName: String) {

    private val LOG_TAG = "Player"
    private val player = MediaPlayer()

    init {
        try {
            player.setDataSource(fileName)
            player.prepare()
            player.setOnSeekCompleteListener{  }
        } catch (e: IOException) {
            Log.e(LOG_TAG, "prepare() failed")
        }
    }

    fun startPlaying() {
        player.start()
    }

    fun pausePlaying() {
        player.pause()
    }

    fun stopPlaying() {
        player.stop()
        player.release()
    }
}