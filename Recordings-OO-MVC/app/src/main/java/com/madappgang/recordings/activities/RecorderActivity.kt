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
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.UI
import java.io.File
import java.util.concurrent.TimeUnit

class RecorderActivity :
    AppCompatActivity(),
    EditableDialogFragment.CompletionHandler,
    EditableDialogFragment.FieldValidationHandler {

    private val permissionsRequestRecordAudio = 3526
    private val saveTrackRequestId = "saveTrackRequestId"

    private val folder by lazy { intent.getParcelableExtra(FOLDER_KEY) as Folder }

    private val uiContext by lazy { UI }
    private val bgContext by lazy { CommonPool }
    private val fileManager by lazy { App.dependencyContainer.fileManager }
    private val recorder by lazy { App.dependencyContainer.recorder }

    private val time by lazy { findViewById<TextView>(R.id.time) }
    private val startRecording by lazy { findViewById<Button>(R.id.startRecording) }
    private val pauseResumeRecording by lazy { findViewById<Button>(R.id.pauseResumeRecording) }
    private val stopRecording by lazy { findViewById<Button>(R.id.stopRecording) }
    private val progressBar by lazy { findViewById<ProgressBar>(R.id.progressBar) }

    private lateinit var track: Track

    private var updateTimeJob: Job? = null
    private var saveTrackJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recorder)

        recorder.reset()
        recorder.status.observe(this, Observer<Recorder.Status> { updateButton() })
        updateTime()
        initButton()
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

    private fun initButton() {
        initStartRecordingButton()
        initPauseResumeRecordingButton()
        initStopRecordingButton()
    }

    private fun initStartRecordingButton() {
        startRecording.setOnClickListener {
            if (isMicrophonePermissionGranted()) {
                startRecording()
            } else {
                requestMicrophonePermission()
            }
        }
    }

    private fun initPauseResumeRecordingButton() {
        pauseResumeRecording.setOnClickListener {
            if (recorder.status.value == Recorder.Status.STARTED) {
                recorder.pause()
                updateTimeJob?.cancel()
            } else if (recorder.status.value == Recorder.Status.PAUSED) {
                updateTimeJob = createUpdateTimeJob()
                recorder.resume()
            }
        }
    }

    private fun initStopRecordingButton() {
        stopRecording.setOnClickListener {
            track = recorder.stop()
            updateTimeJob?.cancel()
            showSaveTrackDialog()
        }
    }

    private fun startRecording() {
        updateTimeJob = createUpdateTimeJob()
        recorder.start()
        updateButton()
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

    private fun updateButton() {
        when (recorder.status.value) {
            Recorder.Status.NOT_STARTED -> applyNotStartedStateForButton()
            Recorder.Status.STARTED -> applyStartedStateForButton()
            Recorder.Status.PAUSED -> applyPausedStateForButton()
            Recorder.Status.COMPLETED -> applyCompletedStateForButton()
        }
    }

    private fun createUpdateTimeJob() = launch(uiContext) {
        while (true) {
            updateTime()
            delay(1, TimeUnit.SECONDS)
        }
    }

    private fun updateTime() {
        time.text = time.formatMilliseconds(recorder.getRecordingTime())
    }

    private fun applyNotStartedStateForButton() {
        startRecording.isEnabled = true
        pauseResumeRecording.isEnabled = false
        pauseResumeRecording.text = getString(R.string.RecorderActivity_pause)
        stopRecording.isEnabled = false
    }

    private fun applyStartedStateForButton() {
        startRecording.isEnabled = false
        pauseResumeRecording.isEnabled = true
        pauseResumeRecording.text = getString(R.string.RecorderActivity_pause)
        stopRecording.isEnabled = true
    }

    private fun applyPausedStateForButton() {
        startRecording.isEnabled = false
        pauseResumeRecording.isEnabled = true
        pauseResumeRecording.text = getString(R.string.RecorderActivity_resume)
        stopRecording.isEnabled = true
    }

    private fun applyCompletedStateForButton() {
        startRecording.isEnabled = false
        pauseResumeRecording.isEnabled = false
        pauseResumeRecording.text = getString(R.string.RecorderActivity_pause)
        stopRecording.isEnabled = false
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