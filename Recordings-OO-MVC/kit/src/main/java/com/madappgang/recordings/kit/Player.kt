/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 6/8/18.
 */

package com.madappgang.recordings.kit

import android.arch.lifecycle.MutableLiveData
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import com.madappgang.recordings.core.Result
import com.madappgang.recordings.core.Track
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.CoroutineDispatcher
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.async
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class Player(
    private val cacheDirectory: File
) : MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {

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
    var onError: ((Throwable) -> Unit)? = null

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

        audioPlayer.setDataSource(track.getFullPath())
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

    fun release() {
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

