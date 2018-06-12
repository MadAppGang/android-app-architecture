package com.madappgang.architecture.recorder.helpers

import android.media.MediaRecorder
import android.os.Handler
import android.os.SystemClock
import android.util.Log
import com.madappgang.architecture.recorder.helpers.FileManager.Companion.mainDirectory
import com.madappgang.architecture.recorder.helpers.FileManager.Companion.recordFormat
import com.madappgang.architecture.recorder.helpers.FileManager.Companion.testRecordName


class Recorder(callback: RecordTimeUpdate) {

    interface RecordTimeUpdate {
        fun onTimeUpdate(time: Long)
    }

    private val LOG_TAG = "Recorder"
    private var fileName: String? = null
    private val recorder = MediaRecorder()
    private val handler = Handler()
    private var startTime = 0L
    private val delay = 100L
    private var currentTime = 0L

    private val updateTimerThread = object : Runnable {
        override fun run() {
            currentTime = SystemClock.uptimeMillis() - startTime
            callback.onTimeUpdate(currentTime)
            handler.postDelayed(this, delay)
        }
    }

    fun init() {
        try {
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile()
            recorder.prepare()
            onStartRecord()
        } catch (e: Throwable) {
            Log.e(LOG_TAG, "prepare() failed")
        }
    }

    private fun setOutputFile() {
        fileName = mainDirectory + "/$testRecordName$recordFormat"
        recorder.setOutputFile(fileName)
    }

    fun onStartRecord() {
        recorder.start()
        startTime = SystemClock.uptimeMillis()
        handler.postDelayed(updateTimerThread, 0)
    }

    fun onStopRecord() {
        recorder.stop()
        recorder.release()
    }
}