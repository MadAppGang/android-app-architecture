/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 7/9/18.
 */

package com.madappgang.recordings.models

import android.arch.lifecycle.MutableLiveData

class Player {

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

    fun play(track: Track) {
        TODO("not implemented")
    }

    fun start() {
        TODO("not implemented")
    }

    fun pause() {
        TODO("not implemented")
    }

    fun stop() {
        TODO("not implemented")
    }

    fun seekTo(millisecond: Int) {
        TODO("not implemented")
    }

    fun reset() {
        TODO("not implemented")
    }

    fun getCurrentPosition(): Int {
        TODO("not implemented")
    }

    fun getDuration(): Int {
        TODO("not implemented")
    }
}