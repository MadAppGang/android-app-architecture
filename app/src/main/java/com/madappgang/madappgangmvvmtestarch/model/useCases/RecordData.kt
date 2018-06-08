package com.madappgang.madappgangmvvmtestarch.model.useCases

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import java.io.File
import android.content.Context.MODE_PRIVATE
import java.io.FileOutputStream


/**
 * Created by Serhii Chaban sc@madappgang.com on 29.05.18.
 */
class RecordData(val filePath: String) {
    val file: File = File(filePath)
    private val recorder = MediaRecorder()
    fun startRecord() {
        if (file.exists().not()) {
            file.createNewFile()
        }
        val outputStream = FileOutputStream(file)
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.HE_AAC)
        recorder.setOutputFile(outputStream.fd)
        recorder.prepare()
        recorder.start()
    }

    fun stopRecord() {
        recorder.stop()
    }

    fun pauseRecord() {
        recorder.stop()
    }

    fun cleanTemtFile() {
        val file = File(filePath)
        if (file.exists()) {
            file.delete()
        }
    }

    fun saveRecordWithName(name: String) {
        val newFile = File(file.absolutePath + "/" + name)
        file.renameTo(newFile)
    }
}