package com.madappgang.madappgangmvvmtestarch.model.service

import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.Observable

interface PlayerService {
    open class ErrorCodes {
        val sourceCorrupredError = 3
        val unknownFormatError = 2
        val sourceDeletedError = 1
    }

    sealed class PlayerState {
        class Initial : PlayerState()
        class Inited : PlayerState()
        class Started : PlayerState()
        class Paused : PlayerState()
        class Stopped : PlayerState()
        class Reseted : PlayerState()
        class Released : PlayerState()
        sealed class Throuble : PlayerState() {
            class PlayerError(errorCode: Int) : Throuble()
            class Error(e: Throwable) : Throuble()
        }
    }

    var playerState: BehaviorRelay<PlayerState>
    val duration: Int
    val currentPosition: Int
    val isPlaying: Boolean
    fun setDataSource(source: String)
    fun seekTo(seek: Int)
    fun startPlay()
    fun start()
    fun stop()
    fun reset()
    fun release()
    fun pause()
}

