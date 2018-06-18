package com.madappgang.madappgangmvvmtestarch.model.service

import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer

/**
 * Created by Serhii Chaban sc@madappgang.com on 08.06.18.
 */
class PlayerServiceImpl : PlayerService {
    override var onComplete: () -> Unit = {}
    override var onError: (errorCode: Int, message: String) -> Boolean = { code, message -> false }
    override var onInit: () -> Unit = {}
    override val duration: Int get() = mediaPlayer.duration
    override val currentPosition: Int get() = mediaPlayer.currentPosition
    override val isPlaying: Boolean get() = mediaPlayer.isPlaying
    override fun setDataSource(source: String) = mediaPlayer.setDataSource(source)
    override fun seekTo(seek: Int) = mediaPlayer.seekTo(seek)
    override fun startPlay() = mediaPlayer.prepareAsync()
    override fun start() = mediaPlayer.start()
    override fun stop() = mediaPlayer.stop()
    override fun reset() = mediaPlayer.reset()
    override fun release() = mediaPlayer.release()
    override fun pause() = mediaPlayer.pause()
     val mediaPlayer by lazy {
        val mediaPlayer = MediaPlayer()
        mediaPlayer.setOnErrorListener { mediaPlayer, code, extra ->
            onError.invoke(code, extra.toString())
        }
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