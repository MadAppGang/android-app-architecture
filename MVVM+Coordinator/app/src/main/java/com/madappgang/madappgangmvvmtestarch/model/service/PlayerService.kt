package com.madappgang.madappgangmvvmtestarch.model.service

interface PlayerService {
    open class ErrorCodes {
        val sourceCorrupredError = 3
        val unknownFormatError = 2
        val sourceDeletedError = 1
    }
    var onComplete: () -> Unit
    var onError: (errorCode: Int, message: String) -> Boolean
    var onInit: () -> Unit
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

