/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 6/11/18.
 */

package com.madappgang.recordings.media

import android.media.MediaRecorder
import android.os.SystemClock
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class AudioRecorder(private val destinationDirectory: File) {

    private var recorder: MediaRecorder? = null
    private lateinit var outputFile: File
    private var startTime = 0L
    private var isStarted = false

    fun startRecording() {
        if (isStarted) {
            return
        }
        recorder = createMediaRecorder()

        recorder?.prepare()
        recorder?.start()
        startTime = SystemClock.elapsedRealtime()
        isStarted = true
    }

    private fun createMediaRecorder() : MediaRecorder {
        val recorder = MediaRecorder()
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        recorder.setAudioEncodingBitRate(64000)
        recorder.setAudioSamplingRate(16000)
        outputFile = getOutputFile()
        recorder.setOutputFile(outputFile.absolutePath)
        return recorder
    }

    fun stopRecording(): String {
        recorder?.stop()
        recorder?.release()
        isStarted = false
        return outputFile.absolutePath
    }

    fun getProgress() = if (isStarted) {
        val currentTime = SystemClock.elapsedRealtime()
        currentTime - startTime
    } else {
        0
    }

    private fun getOutputFile(): File {
        val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmssSSS")
        return File(destinationDirectory, "recording_${dateFormat.format(Date())}.m4a")
    }
}