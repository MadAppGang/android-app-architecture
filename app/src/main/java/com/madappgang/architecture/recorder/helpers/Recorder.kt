package com.madappgang.architecture.recorder.helpers

import android.content.Context
import android.media.MediaRecorder
import android.util.Log
import java.io.IOException


class Recorder(val context: Context) {

    private val LOG_TAG = "Recorder"
    private var fileName: String? = null
    private val recorder = MediaRecorder()

    init {
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        setOutputFile()
        try {
            recorder.prepare()
        } catch (e: IOException) {
            Log.e(LOG_TAG, "prepare() failed")
        }
    }

    private fun setOutputFile() {
        // Record to the external cache directory for visibility
        fileName = context.externalCacheDir.absolutePath
        fileName += "/audioRecord.3gp"
        recorder.setOutputFile(fileName)
    }

    fun onStartRecord() {
        recorder.start()
    }

    fun onStopRecord() {
        recorder.stop()
        recorder.release()
    }
}