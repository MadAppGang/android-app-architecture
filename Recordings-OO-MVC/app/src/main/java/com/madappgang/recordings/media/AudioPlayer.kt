/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 6/12/18.
 */

package com.madappgang.recordings.media

import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer

class AudioPlayer : MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {
    var onPrepared: () -> Unit = {}
    var onCompleted: () -> Unit = {}

    private val player: MediaPlayer = MediaPlayer()

    init {
        val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                .build()
        player.setAudioAttributes(audioAttributes)
        player.setOnPreparedListener(this)
        player.isLooping = false
    }

    fun prepare(dataSource: String) {
        player.setDataSource(dataSource)
        player.prepareAsync()
    }

    fun start() {
        player.start()
        player.setOnCompletionListener(this)
    }

    fun stop() {
        player.setOnCompletionListener(null)
        player.stop()
        player.release()
    }

    fun pause() {
        if (player.isPlaying) {
            player.pause()
        }
    }

    fun seekTo(millisecond: Int) {
        player.seekTo(millisecond)
    }

    fun getCurrentPosition() = player.currentPosition.toLong()

    fun getDuration() = player.duration.toLong()

    override fun onPrepared(mp: MediaPlayer?) {
        onPrepared.invoke()
    }

    override fun onCompletion(mp: MediaPlayer?) {
        onCompleted.invoke()
    }
}