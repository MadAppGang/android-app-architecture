package com.madappgang.architecture.recorder.data.repositories

/**
 * Created by Bohdan Shchavinskiy <bogdan@madappgang.com> on 04.07.2018.
 */
interface RecordingRepository {
    interface RecordTimeUpdate {
        fun onTimeUpdate(time: Long)
    }

    fun init(callback: RecordTimeUpdate)
    fun onStopRecord()
}