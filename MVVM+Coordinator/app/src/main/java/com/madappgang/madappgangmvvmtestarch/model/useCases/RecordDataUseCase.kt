package com.madappgang.madappgangmvvmtestarch.model.useCases

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import java.io.File
import android.content.Context.MODE_PRIVATE
import java.io.FileInputStream
import java.io.FileOutputStream


/**
 * Created by Serhii Chaban sc@madappgang.com on 29.05.18.
 */
class RecordDataUseCase(private val filePath: String) {
    val fileAbsolutePaths = "$filePath/tempFile.mp4"
    val file: File = File(fileAbsolutePaths)
    private var recorder: MediaRecorder = MediaRecorder()

    fun startRecord() {
        try {
            if (file.exists().not()) {
                file.createNewFile()
            }
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            recorder.setOutputFile(file.absolutePath)
            recorder.prepare()
            recorder.start()
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    fun stopRecord() {
        try {
            recorder.stop()
        } catch (e: Throwable) {

        }
    }

    fun releaseData() {
        val file = File(fileAbsolutePaths)
        if (file.exists()) {
            file.delete()
        }
        recorder.reset()
        recorder.release()
    }

    fun saveRecordWithName(name: String) {
        val newFile = File(file.absolutePath + "/" + name)
        file.renameTo(newFile)
    }
}