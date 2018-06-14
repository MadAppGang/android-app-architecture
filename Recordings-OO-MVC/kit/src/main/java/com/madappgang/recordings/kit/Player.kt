/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 6/8/18.
 */

package com.madappgang.recordings.kit

import android.arch.lifecycle.MutableLiveData
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
    private val cacheDirectory: File,
    private val network: Network
) {
    var state: MutableLiveData<State> = MutableLiveData()
    var onError: ((Throwable) -> Unit)? = null

    private val audioPlayer = AudioPlayer()
    private var tempFile: File? = null
    private var loadFileJob: Job? = null

    private val bgContext: CoroutineDispatcher by lazy { CommonPool }

    init {
        state.value = State.NOT_STARTED

        audioPlayer.onPrepared = { start() }
        audioPlayer.onCompleted = { state.value = State.COMPLETED }
    }

    fun play(track: Track) {
        audioPlayer.reset()
        state.value = State.PREPARING
        val destination = createCacheDestanition(track)

        loadFileJob?.cancel()
        loadFileJob = async(bgContext) {
            val result = network.downloadFile(track.path, destination)

            when (result) {
                is Result.Success -> audioPlayer.prepare(destination.absolutePath)
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
        audioPlayer.seekTo(audioPlayer.getCurrentPosition() * -1)
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
        state.value == State.PREPARING ||
        state.value == State.STOPPED ||
        state.value == State.COMPLETED
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

    private fun createCacheDestanition(track: Track): File {
        val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmssSSS")
        val fileName = "track_${track.folderId}_${track.name}_${dateFormat.format(Date())}.m4a"
        return File(cacheDirectory, fileName)
    }

    enum class State {
        NOT_STARTED,
        PREPARING,
        PLAYING,
        PAUSED,
        STOPPED,
        COMPLETED
    }
}

