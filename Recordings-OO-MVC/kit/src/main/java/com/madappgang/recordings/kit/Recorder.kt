/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 6/8/18.
 */

package com.madappgang.recordings.kit

import android.arch.lifecycle.MutableLiveData
import com.googlecode.mp4parser.authoring.Movie
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator
import com.googlecode.mp4parser.authoring.tracks.AppendTrack
import com.madappgang.recordings.core.Track
import java.io.File
import java.io.RandomAccessFile
import java.text.SimpleDateFormat
import java.util.*


class Recorder(cacheDirectory: File) {

    var status: MutableLiveData<Status> = MutableLiveData()

    private val sourceFiles = mutableListOf<String>()
    private var audioRecorder = AudioRecorder(cacheDirectory)
    private var recordingTime = 0

    fun start() {
        if (status.value != Status.NOT_STARTED) {
            throw IllegalStateException()
        }
        audioRecorder.startRecording()
        status.value = Status.STARTED
        recordingTime = 0
    }

    fun pause() {
        if (status.value != Status.STARTED) {
            throw IllegalStateException()
        }
        recordingTime += audioRecorder.getProgress()
        val filePath = audioRecorder.stopRecording()
        addPart(File(filePath))
        status.value = Status.PAUSED
    }

    fun resume() {
        if (status.value != Status.PAUSED) {
            throw IllegalStateException()
        }
        audioRecorder.startRecording()
        status.value = Status.STARTED
    }

    fun stop(): Track {
        if (status.value != Status.STARTED && status.value != Status.PAUSED) {
            throw IllegalStateException()
        }
        if (status.value == Status.STARTED) {
            recordingTime += audioRecorder.getProgress()
            val filePath = audioRecorder.stopRecording()
            addPart(File(filePath))
        }
        val outputFileName = getOutputFileName()
        mergeAudioFiles(sourceFiles, outputFileName)
        removeParts(sourceFiles)
        status.value = Status.COMPLETED
        return Track(path = outputFileName)
    }

    fun reset() {
        recordingTime = 0
        if (audioRecorder.isStarted) {
            val filePath = audioRecorder.stopRecording()
            addPart(File(filePath))
        }
        removeParts(sourceFiles)
        sourceFiles.clear()
        audioRecorder.release()
        status.value = Status.NOT_STARTED
    }

    fun getRecordingTime() = recordingTime + audioRecorder.getProgress()

    private fun addPart(file: File) {
        sourceFiles.add(file.absolutePath)
    }

    private fun getOutputFileName(): String {
        val folder = File(sourceFiles.first()).absolutePath
        val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmssSSS")
        return "${folder}recording_${dateFormat.format(Date())}.m4a"
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
        val fileChannel = RandomAccessFile(String.format(outputFileName), "rw").channel
        container.writeContainer(fileChannel)
        fileChannel.close()
    }

    private fun removeParts(sourceFiles: List<String>) = sourceFiles
            .map { File(it) }
            .filter { it.exists() }
            .forEach {
                it.delete()
            }

    enum class Status {
        NOT_STARTED,
        STARTED,
        PAUSED,
        COMPLETED
    }
}