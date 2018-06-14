/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 6/7/18.
 */

package com.madappgang.recordings.activities

import android.Manifest
import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import com.madappgang.recordings.R
import com.madappgang.recordings.application.App
import com.madappgang.recordings.core.Folder
import com.madappgang.recordings.core.Result
import com.madappgang.recordings.core.Track
import com.madappgang.recordings.dialogs.EditableDialogFragment
import com.madappgang.recordings.extensions.formatMilliseconds
import com.madappgang.recordings.extensions.makeGone
import com.madappgang.recordings.extensions.makeVisible
import com.madappgang.recordings.extensions.showError
import com.madappgang.recordings.kit.Recorder
import com.madappgang.recordings.kit.validName
import com.madappgang.recordings.layoutmanagers.RecorderView
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.UI
import java.io.File
import java.util.concurrent.TimeUnit

class RecorderActivity :
    AppCompatActivity(),
    EditableDialogFragment.CompletionHandler,
    EditableDialogFragment.FieldValidationHandler {

    private val recorderView by lazy { findViewById<RecorderView>(R.id.recorderView) }
    private val progressBar by lazy { findViewById<ProgressBar>(R.id.progressBar) }

    private val recorder by lazy { App.dependencyContainer.recorder }
    private val fileManager by lazy { App.dependencyContainer.fileManager }

    private lateinit var track: Track

    private var updateTimeJob: Job? = null
    private var saveTrackJob: Job? = null
    private val uiContext by lazy { UI }
    private val bgContext by lazy { CommonPool }

    private val folder by lazy { intent.getParcelableExtra(FOLDER_KEY) as Folder }

    private val permissionsRequestRecordAudio = 3526
    private val saveTrackRequestId = "saveTrackRequestId"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recorder)

        recorderView.onStartRecording = {
            if (isMicrophonePermissionGranted()) {
                startRecording()
            } else {
                requestMicrophonePermission()
            }
        }
        recorderView.onStopRecording = { stopRecording() }
        recorderView.onPauseResumeRecording = { pauseResumeRecording() }

        recorder.reset()
        recorder.status.observe(this, Observer<Recorder.Status> {
            updateUi()
            updateUiJob()
        })

        updateUi()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == permissionsRequestRecordAudio &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            startRecording()

        }
    }

    override fun onDialogPositiveClick(requestId: String, value: String) {
        if (requestId == saveTrackRequestId) {
            track.folderId = folder.id
            track.name = value
            saveTrackJob?.cancel()
            saveTrackJob = saveTrack(track)
        }
    }

    override fun onDialogNegativeClick(requestId: String) {
        if (requestId == saveTrackRequestId) {
            removeTrack()
            setResult(Activity.RESULT_CANCELED)
            onBackPressed()
        }
    }

    override fun onValidField(requestId: String, value: String) = try {
        fileManager.validName(value)
        true
    } catch (e: Throwable) {
        false
    }

    override fun onDestroy() {
        super.onDestroy()
        updateTimeJob?.cancel()
        recorder.reset()
        removeTrack()
    }

    private fun pauseResumeRecording() {
        if (recorder.status.value == Recorder.Status.STARTED) {
            recorder.pause()
        } else if (recorder.status.value == Recorder.Status.PAUSED) {
            recorder.resume()
        }
    }

    private fun startRecording() {
        recorder.start()
    }

    private fun stopRecording() {
        track = recorder.stop()
        showSaveTrackDialog()
    }

    private fun isMicrophonePermissionGranted() =
        ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) ==
            PackageManager.PERMISSION_GRANTED

    private fun requestMicrophonePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.RECORD_AUDIO
            )
        ) {
            AlertDialog.Builder(this@RecorderActivity)
                .setMessage(R.string.RecorderActivity_explanation_permission_request)
                .setPositiveButton(R.string.RecorderActivity_ok, { _, _ ->
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.RECORD_AUDIO),
                        permissionsRequestRecordAudio
                    )
                })
                .setNegativeButton(R.string.RecorderActivity_cancel, { _, _ -> })
                .show()

        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                permissionsRequestRecordAudio
            )
        }
    }

    private fun updateUiJob() {
        updateTimeJob?.cancel()
        if (recorder.status.value == Recorder.Status.STARTED) {
            updateTimeJob = createUpdateTimeJob()
        }
    }

    private fun createUpdateTimeJob() = launch(uiContext) {
        while (true) {
            delay(1, TimeUnit.SECONDS)
            updateUi()
        }
    }

    private fun updateUi() {
        recorder.status.value?.let { recorderView.setStatus(it) }
        recorderView.setTime(recorder.getRecordingTime())
    }

    private fun showSaveTrackDialog(defaultName: String = "") {
        val dialog = EditableDialogFragment.newInstance(
            saveTrackRequestId,
            R.string.RecorderActivity_save_recording,
            R.string.RecorderActivity_enter_name,
            R.string.RecorderActivity_save,
            R.string.RecorderActivity_cancel,
            defaultName
        )
        dialog.show(supportFragmentManager, "SaveTrackDialogTag")
    }

    private fun saveTrack(track: Track) = launch(uiContext) {
        progressBar.makeVisible()

        val result = async(bgContext) { fileManager.add(track, Track::class.java) }.await()

        when (result) {
            is Result.Success -> {
                setResult(Activity.RESULT_OK)
                onBackPressed()
            }
            is Result.Failure -> {
                showSaveTrackDialog(track.name)
                showError(result.throwable)
            }
        }
        progressBar.makeGone()
    }

    private fun removeTrack() {
        if (::track.isInitialized) {
            val tmpTrack = File(track.path)
            if (tmpTrack.exists()) {
                tmpTrack.delete()
            }
        }
    }

    companion object {

        private val FOLDER_KEY = "folder_key"

        fun startForResult(activity: AppCompatActivity, folder: Folder, requestCode: Int) {
            val intent = Intent(activity, RecorderActivity::class.java)
            intent.putExtra(FOLDER_KEY, folder)
            activity.startActivityForResult(intent, requestCode)
        }
    }
}