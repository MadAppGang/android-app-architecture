package com.madappgang.madappgangmvvmtestarch.model.useCases

import android.media.MediaRecorder
import android.util.Log
import com.madappgang.madappgangmvvmtestarch.model.useCases.RecordDataUseCase.RecorderState.*
import java.io.File
import java.io.FileNotFoundException


/**
 * Created by Serhii Chaban sc@madappgang.com on 29.05.18.
 */
class RecordDataUseCase(private val filePath: String) {
    val fileExtention = "m4a"
    val file: File = File(filePath, "tempFile.$fileExtention")
    private var recorder: MediaRecorder = MediaRecorder()

    sealed class RecorderState {
        class Initial : RecorderState()
        class FileSaved : RecorderState()
        class Started : RecorderState()
        class Paused : RecorderState()
        class Stopped : RecorderState()
        sealed class Throuble : RecorderState() {
            class PlayerError(errorCode: Int) : Throuble()
            class Error(e: Throwable) : Throuble()
        }
    }

    var recorderStateCallback: (RecorderState) -> Unit = {}
    private var recorderState: RecorderState = Initial()
        set(value) {
            recorderStateCallback.invoke(value)
            field = value
        }

    fun startRecord() {
        try {
            if (file.exists().not()) {
                file.createNewFile()
            } else {
                file.delete()
                file.createNewFile()
            }
            recorder.setOnErrorListener { mr, what, extra ->
                Log.e("recorder", what.toString())
                recorderState = RecorderState.Throuble.PlayerError(what)
            }
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            recorder.setOutputFile(file.absolutePath)
            recorder.prepare()
            recorder.start()
            recorderState = RecorderState.Started()
        } catch (e: Throwable) {
            e.printStackTrace()
            recorderState = RecorderState.Throuble.Error(e)
        }
    }

    fun stopRecord() {
        recorderState = try {
            recorder.stop()
            RecorderState.Stopped()

        } catch (e: Throwable) {
            Log.e("recorder", "stopped error", e)
            RecorderState.Throuble.Error(e)
        }
    }

    fun pauseRecord() {
        recorderState = try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                recorder.pause()
                Paused()

            } else {
                recorder.stop()
                Stopped()
            }
        } catch (e: Throwable) {
            Log.e("recorder", "stopped error", e)
            RecorderState.Throuble.Error(e)
        }
    }

    fun releaseData() {
        if (file.exists()) {
            file.delete()
        }
        recorder.reset()
        recorder.release()
        recorderState = Initial()
    }

    fun saveRecordWithName(name: String) {
        val newFile = File(file.parent, "$name.$fileExtention")
        val copyToFile = file.copyTo(newFile, true)
        if (!copyToFile.exists()) {
            throw FileNotFoundException()
        } else {
            recorderState = FileSaved()
        }
    }
}