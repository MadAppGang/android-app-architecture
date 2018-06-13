/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 6/8/18.
 */

package com.madappgang.recordings.kit

import com.madappgang.recordings.core.Track
import com.madappgang.recordings.network.Network
import com.madappgang.recordings.core.Result
import kotlinx.coroutines.experimental.CoroutineDispatcher
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.async
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class Player(
    private val tempDirectory: File,
    private val network: Network,
    private val bgContext: CoroutineDispatcher
) {

    var onPlay: ((File) -> Unit)? = null
    var onError: ((Throwable) -> Unit)? = null

    private var tempFile: File? = null
    private var loadFileJob: Job? = null

    fun play(track: Track) {
        val destination = getTempFile(track)

        loadFileJob = async(bgContext) {
            val result = network.downloadFile(track.path, destination)

            when (result) {
                is Result.Success -> onPlay?.invoke(destination)
                is Result.Failure -> onError?.invoke(result.throwable)
            }
            tempFile = destination
        }

    }

    private fun getTempFile(track: Track): File {
        val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmssSSS")
        val fileName = "track_${track.folderId}_${track.name}_${dateFormat.format(Date())}.m4a"
        return File(tempDirectory, fileName)
    }

    fun stop() {
        loadFileJob?.cancel()
        tempFile?.let {
            if (it.exists()) {
                it.delete()
            }
        }
    }
}