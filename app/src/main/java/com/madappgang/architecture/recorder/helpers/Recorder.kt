package com.madappgang.architecture.recorder.helpers

import android.content.Context
import android.media.MediaRecorder
import android.os.Environment
import android.os.Handler
import android.os.SystemClock
import android.util.Log
import java.io.File


class Recorder(val context: Context, callback: RecordTimeUpdate) {

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
        // Record to the external cache directory for visibility
        File(timeDirectory).mkdirs()
        fileName = timeDirectory + "/$recordName$recordFormat"
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

    companion object {
        val mainDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).absolutePath + "/Records"
        val timeDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).absolutePath + "/Records/cache"
        val recordName = "audioRecord"
        val recordFormat = ".3gp"
    }
}