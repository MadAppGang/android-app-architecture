/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 6/8/18.
 */

package com.madappgang.recordings.kit

import android.arch.lifecycle.MutableLiveData
import android.media.MediaRecorder
import android.os.SystemClock
import com.googlecode.mp4parser.authoring.Movie
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator
import com.googlecode.mp4parser.authoring.tracks.AppendTrack
import com.madappgang.recordings.core.Track
import java.io.File
import java.io.RandomAccessFile
import java.text.SimpleDateFormat
import java.util.*


class Recorder(val cacheDirectory: File) {

    enum class Status {
        NOT_STARTED,
        STARTED,
        PAUSED,
        COMPLETED
    }

    var status: MutableLiveData<Status> = MutableLiveData()

    private val sourceFiles = mutableListOf<String>()
    private var recordsPartsTime = 0

    private var partRecorder: MediaRecorder? = null
    private lateinit var part: File
    private var startTime = 0L

    fun start() {
        if (status.value != Status.NOT_STARTED) {
            throw IllegalStateException()
        }
        startPartRecording()
        status.value = Status.STARTED
        recordsPartsTime = 0
    }

    fun pause() {
        if (status.value != Status.STARTED) {
            throw IllegalStateException()
        }
        stopPartRecording()
        addPart(part)
        status.value = Status.PAUSED
    }

    fun resume() {
        if (status.value != Status.PAUSED) {
            throw IllegalStateException()
        }
        startPartRecording()
        status.value = Status.STARTED
    }

    fun stop(): Track {
        if (status.value != Status.STARTED && status.value != Status.PAUSED) {
            throw IllegalStateException()
        }
        if (status.value == Status.STARTED) {
            stopPartRecording()
            addPart(part)
        }
        val outputFileName = getOutputFile().absolutePath
        mergeAudioFiles(sourceFiles, outputFileName)
        removeParts(sourceFiles)
        status.value = Status.COMPLETED
        return Track(path = outputFileName)
    }

    fun reset() {
        recordsPartsTime = 0
        if (status.value == Status.STARTED) {
            stopPartRecording()
            addPart(part)
        }
        removeParts(sourceFiles)
        sourceFiles.clear()
        partRecorder?.release()
        status.value = Status.NOT_STARTED
    }

    fun getRecordingTime() = recordsPartsTime + computeTimePartRecording()

    private fun addPart(file: File) {
        sourceFiles.add(file.absolutePath)
    }

    private fun mergeAudioFiles(sourceFiles: List<String>, outputFileName: String) {
        val listTracks = sourceFiles.map { MovieCreator.build(it) }
            .flatMap { it.tracks }
            .filter { it.handler == "soun" }
            .toList()

        val outputMovie = Movie()
        if (!listTracks.isEmpty()) {
            outputMovie.addTrack(AppendTrack(*listTracks.toTypedArray()))
        }
        val container = DefaultMp4Builder().build(outputMovie)
        val fileChannel = RandomAccessFile(outputFileName, "rw").channel
        container.writeContainer(fileChannel)
        fileChannel.close()
    }

    private fun removeParts(sourceFiles: List<String>) = sourceFiles
        .map { File(it) }
        .filter { it.exists() }
        .forEach {
            it.delete()
        }

    private fun startPartRecording() {
        partRecorder = createMediaRecorder()
        partRecorder?.prepare()
        partRecorder?.start()
        startTime = SystemClock.elapsedRealtime()
    }

    private fun stopPartRecording() {
        partRecorder?.stop()
        partRecorder?.release()
        recordsPartsTime += computeTimePartRecording()
    }

    private fun computeTimePartRecording() = if (status.value == Status.STARTED) {
        val currentTime = SystemClock.elapsedRealtime()
        (currentTime - startTime).toInt()
    } else {
        0
    }

    private fun createMediaRecorder(): MediaRecorder {
        val recorder = MediaRecorder()
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        recorder.setAudioEncodingBitRate(64000)
        recorder.setAudioSamplingRate(16000)
        part = getOutputFile()
        recorder.setOutputFile(part.absolutePath)
        return recorder
    }

    private fun getOutputFile(): File {
        val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmssSSS")
        return File(cacheDirectory, "recording_${dateFormat.format(Date())}.m4a")
    }
}