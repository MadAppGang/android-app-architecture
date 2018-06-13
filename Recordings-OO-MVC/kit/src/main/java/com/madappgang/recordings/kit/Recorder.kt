/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 6/8/18.
 */

package com.madappgang.recordings.kit

import com.googlecode.mp4parser.authoring.Movie
import com.madappgang.recordings.core.Track
import java.io.File
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder
import com.googlecode.mp4parser.authoring.tracks.AppendTrack
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator
import java.io.RandomAccessFile
import java.text.SimpleDateFormat
import java.util.*


class Recorder {

    private val sourceFiles = mutableListOf<String>()

    fun start() {
        removeParts(sourceFiles)
        sourceFiles.clear()
    }

    fun addPart(file: File) {
        sourceFiles.add(file.absolutePath)
    }

    fun stop(): Track {
        val outputFileName = getOutputFileName()
        mergeAudioFiles(sourceFiles, outputFileName)
        removeParts(sourceFiles)
        return Track(path = outputFileName)
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
}