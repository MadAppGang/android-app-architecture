package com.madappgang.architecture.recorder.helpers

import android.media.MediaPlayer
import android.util.Log
import java.io.IOException

class Player(private val fileName: String, callback: PlayerCallback) {

    interface PlayerCallback {
        fun setDuration(duration: Int)
    }

    private val LOG_TAG = "Player"
    private val player = MediaPlayer()

    init {
        try {
            player.setDataSource(fileName)
            player.prepare()
            callback.setDuration(player.duration)
            player.setOnSeekCompleteListener { }
        } catch (e: IOException) {
            Log.e(LOG_TAG, "prepare() failed")
        }
    }

    fun play() {
        player.start()
    }

    fun pause() {
        player.pause()
    }

    fun getCurrentPosition(): Int = player.currentPosition

    fun isPlaying(): Boolean = player.isPlaying

    fun seekTo(time: Int) {
        player.seekTo(time)
    }

    fun stop() {
        player.stop()
    }

    fun destroy() {
        player.release()
    }
}