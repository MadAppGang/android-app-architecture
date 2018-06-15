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
import com.madappgang.recordings.network.Network
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.CoroutineDispatcher
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.async
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class Player(
    private val cacheDirectory: File, private val network: Network
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
    private var tempFile: File? = null
    private var loadFileJob: Job? = null

    private val bgContext: CoroutineDispatcher by lazy { CommonPool }

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
        val destination = createCacheDestanition(track)

        loadFileJob?.cancel()
        loadFileJob = async(bgContext) {
            val result = network.downloadFile(track.path, destination)

            when (result) {
                is Result.Success -> {
                    audioPlayer.setDataSource(destination.absolutePath)
                    audioPlayer.prepareAsync()
                }
                is Result.Failure -> onError?.invoke(result.throwable)
            }
            tempFile = destination
        }

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
        loadFileJob?.cancel()
        tempFile?.let {
            if (it.exists()) {
                it.delete()
            }
        }
        state.value = State.NOT_STARTED
    }

    fun getCurrentPosition() = if (state.value == State.NOT_STARTED ||
        state.value == State.PREPARING
    ) {
        0
    } else {
        audioPlayer.getCurrentPosition()
    }

    fun getDuration() = if (state.value == State.NOT_STARTED ||
        state.value == State.PREPARING
    ) {
        0
    } else {
        audioPlayer.getDuration()
    }

    override fun onPrepared(mp: MediaPlayer?) {
        start()
    }

    override fun onCompletion(mp: MediaPlayer?) {
        state.value = State.COMPLETED
    }

    private fun createCacheDestanition(track: Track): File {
        val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmssSSS")
        val fileName = "track_${track.folderId}_${track.name}_${dateFormat.format(Date())}.m4a"
        return File(cacheDirectory, fileName)
    }
}

