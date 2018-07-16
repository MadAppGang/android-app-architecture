/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 7/9/18.
 */

package com.madappgang.recordings.models

import android.arch.lifecycle.MutableLiveData
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer

class Player : MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {

    enum class State {
        /**
         * Default state of just created [Player]
         */
        NOT_STARTED,
        /**
         * Preparing track for playing
         */
        PREPARING,
        /**
         * Playing track
         */
        PLAYING,
        /**
         * Paused track
         */
        PAUSED,
        /**
         * Roll back to start track and stop playing
         */
        STOPPED,
        /**
         * Track playing completed
         */
        COMPLETED
    }

    var state: MutableLiveData<State> = MutableLiveData()

    private val audioPlayer: MediaPlayer = MediaPlayer()

    init {
        state.value = State.NOT_STARTED

        val audioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .setLegacyStreamType(AudioManager.STREAM_MUSIC)
            .build()

        audioPlayer.setAudioAttributes(audioAttributes)
        audioPlayer.setOnPreparedListener(this)
        audioPlayer.setOnCompletionListener(this)
        audioPlayer.isLooping = false
    }

    fun play(track: Track) {
        audioPlayer.reset()
        state.value = State.PREPARING

        audioPlayer.setDataSource(track.fullPath)
        audioPlayer.prepareAsync()

    }

    fun start() {
        audioPlayer.start()
        state.value = State.PLAYING
    }

    fun pause() {
        audioPlayer.pause()
        state.value = State.PAUSED
    }

    fun stop() {
        state.value = State.STOPPED
        audioPlayer.pause()
        val startPosition = audioPlayer.currentPosition * -1
        audioPlayer.seekTo(startPosition)
    }

    fun seekTo(millisecond: Int) {
        audioPlayer.seekTo(millisecond)
    }

    fun reset() {
        audioPlayer.reset()
        state.value = State.NOT_STARTED
    }

    fun getCurrentPosition(): Int {
        val isProgressZero = state.value == State.NOT_STARTED || state.value == State.PREPARING

        return if (isProgressZero) 0 else audioPlayer.currentPosition
    }

    fun getDuration(): Int {
        val isDurationZero = state.value == State.NOT_STARTED || state.value == State.PREPARING
        return if (isDurationZero) 0 else audioPlayer.duration
    }

    override fun onPrepared(mp: MediaPlayer?) {
        start()
    }

    override fun onCompletion(mp: MediaPlayer?) {
        state.value = State.COMPLETED
    }
}