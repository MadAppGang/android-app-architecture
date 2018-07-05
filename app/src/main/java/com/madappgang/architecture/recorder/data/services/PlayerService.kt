package com.madappgang.architecture.recorder.data.services

/**
 * Created by Bohdan Shchavinskiy <bogdan@madappgang.com> on 04.07.2018.
 */
interface PlayerService {
    fun init(fileName: String, callback: PlayerCallback)
    fun getCurrentPosition(): Int
    fun isPlaying(): Boolean
    fun play()
    fun pause()
    fun seekTo(time: Int)
    fun release()

    interface PlayerCallback {
        fun setDuration(duration: Int)
    }
}