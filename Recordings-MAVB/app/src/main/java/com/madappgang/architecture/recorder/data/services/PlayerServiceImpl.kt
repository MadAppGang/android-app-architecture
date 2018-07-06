package com.madappgang.architecture.recorder.data.services

import android.media.MediaPlayer
import android.util.Log
import com.madappgang.architecture.recorder.data.services.PlayerService.PlayerCallback
import java.io.IOException

class PlayerServiceImpl : PlayerService {

    private val LOG_TAG = "PlayerServiceImpl"
    private lateinit var fileName: String
    private lateinit var callback: PlayerService.PlayerCallback
    private var player: MediaPlayer? = null

    override fun init(fileName: String, callback: PlayerCallback) {
        this.fileName = fileName
        this.callback = callback
        if (player == null) {
            try {
                player = MediaPlayer()
                player?.setDataSource(fileName)
                player?.prepare()
            } catch (e: IOException) {
                Log.e(LOG_TAG, "prepare() failed")
            }
        }
        callback.setDuration(player?.duration ?: 0)
    }

    override fun getCurrentPosition(): Int = player?.currentPosition ?: 0

    override fun play() {
        player?.start()
    }

    override fun pause() {
        player?.pause()
    }

    override fun seekTo(time: Int) {
        player?.seekTo(time)
    }

    override fun release() {
        player?.release()
        player = null
    }

    override fun isPlaying(): Boolean = try {
        player?.isPlaying ?: false
    } catch (e: Throwable) {
        false
    }
}