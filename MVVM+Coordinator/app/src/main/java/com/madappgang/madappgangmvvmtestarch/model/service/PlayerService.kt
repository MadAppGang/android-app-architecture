package com.madappgang.madappgangmvvmtestarch.model.service

import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer

/**
 * Created by Serhii Chaban sc@madappgang.com on 08.06.18.
 */
class PlayerService() {
    var onComplete: () -> Unit = {}
    var onInit: () -> Unit = {}
    val duration: Int get() = mediaPlayer.duration
    val currentPosition: Int get() = mediaPlayer.currentPosition
    val isPlaying: Boolean get() = mediaPlayer.isPlaying
    fun serDataSource(source: String) = mediaPlayer.setDataSource(source)
    fun seekTo(seek: Int) = mediaPlayer.seekTo(seek)
    fun startPlay() = mediaPlayer.prepareAsync()
    fun start() = mediaPlayer.start()
    fun stop() = mediaPlayer.stop()
    fun reset() = mediaPlayer.reset()
    fun release() = mediaPlayer.release()
    fun pause() = mediaPlayer.pause()

    private val mediaPlayer by lazy {
        val mediaPlayer = MediaPlayer()
        mediaPlayer.setOnPreparedListener {
            it.start()
            onInit.invoke()
        }
        mediaPlayer.setOnCompletionListener {
            onComplete.invoke()
        }

        mediaPlayer.setAudioAttributes(AudioAttributes.Builder().setLegacyStreamType(AudioManager.STREAM_MUSIC).build())
        mediaPlayer
    }
}