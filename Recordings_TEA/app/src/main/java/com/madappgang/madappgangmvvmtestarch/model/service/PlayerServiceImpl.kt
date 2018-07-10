package com.madappgang.madappgangmvvmtestarch.model.service

import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.Observable

/**
 * Created by Serhii Chaban sc@madappgang.com on 08.06.18.
 */
class PlayerServiceImpl : PlayerService {
    override var playerState: BehaviorRelay<PlayerService.PlayerState> = BehaviorRelay.create()
    override val duration: Int get() = mediaPlayer.duration
    override val currentPosition: Int get() = mediaPlayer.currentPosition
    override val isPlaying: Boolean get() = mediaPlayer.isPlaying
    override fun setDataSource(source: String) = mediaPlayer.setDataSource(source)
    override fun seekTo(seek: Int) = mediaPlayer.seekTo(seek)
    override fun startPlay() = mediaPlayer.prepareAsync()
    override fun start() {
        mediaPlayer.start()
        playerState?.accept(PlayerService.PlayerState.Started())
    }

    override fun stop() {
        mediaPlayer.stop()
        playerState?.accept(PlayerService.PlayerState.Stopped())
    }

    override fun reset() {
        mediaPlayer.reset()
        playerState?.accept(PlayerService.PlayerState.Reseted())
    }

    override fun release() {
        mediaPlayer.release()
        playerState?.accept(PlayerService.PlayerState.Released())

    }

    override fun pause() {
        mediaPlayer.pause()
        playerState?.accept(PlayerService.PlayerState.Paused())
    }

    val mediaPlayer by lazy {
        val mediaPlayer = MediaPlayer()
      /*  mediaPlayer.setOnErrorListener { mediaPlayer, code, extra ->
            playerState?.accept(PlayerService.PlayerState.Throuble.PlayerError(code))
            return@setOnErrorListener true
            //onError.invoke(code, extra.toString())
        }*/
        mediaPlayer.setOnPreparedListener {
            playerState.accept(PlayerService.PlayerState.Inited())
            it.start()
            playerState.accept(PlayerService.PlayerState.Started())
        }
        mediaPlayer.setOnCompletionListener {
            playerState.accept(PlayerService.PlayerState.Stopped())
        }

        mediaPlayer.setAudioAttributes(AudioAttributes.Builder().setLegacyStreamType(AudioManager.STREAM_MUSIC).build())
        mediaPlayer
    }
}