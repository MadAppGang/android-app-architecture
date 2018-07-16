/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 7/9/18.
 */

package com.madappgang.recordings.kit

import android.arch.lifecycle.MutableLiveData
import com.madappgang.recordings.core.Track
import java.io.File

class Recorder(val cacheDirectory: File) {

    enum class Status {
        /**
         * Default state of just created [Recorder]
         */
        NOT_STARTED,
        /**
         * Recording track
         */
        STARTED,
        /**
         * Paused recording
         */
        PAUSED,
        /**
         * Track recording completed
         */
        COMPLETED
    }

    var status: MutableLiveData<Status> = MutableLiveData()

    fun start() {
        TODO("not implemented")
    }

    fun pause() {
        TODO("not implemented")
    }

    fun resume() {
        TODO("not implemented")
    }

    fun stop(): Track {
        TODO("not implemented")
    }

    fun reset() {
        TODO("not implemented")
    }

    fun getRecordingTime(): Long {
        TODO("not implemented")
    }
}