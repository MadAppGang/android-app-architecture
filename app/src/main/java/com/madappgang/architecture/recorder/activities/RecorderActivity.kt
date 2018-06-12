package com.madappgang.architecture.recorder.activities

import android.Manifest
import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.madappgang.architecture.recorder.AppInstance
import com.madappgang.architecture.recorder.R
import com.madappgang.architecture.recorder.helpers.Recorder
import com.madappgang.architecture.recorder.view_state_model.FolderViewState
import com.madappgang.architecture.recorder.view_state_model.RecorderViewState
import kotlinx.android.synthetic.main.activity_folder.*
import kotlinx.android.synthetic.main.activity_recorder.*
import java.io.File


class RecorderActivity : AppCompatActivity(), Recorder.RecordTimeUpdate {

    private val LOG_TAG = "RecorderActivity"
    private val REQUEST_RECORD_AUDIO_PERMISSION = 200
    private val permissions = arrayOf<String>(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
    private var permissionToRecordAccepted = false
    private val recorder = Recorder(this)
    private val viewStateStore = AppInstance.appInstance.viewStateStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recorder)
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION)

        viewStateStore.recorderViewState.observe(this, Observer<RecorderViewState> {
            it?.let { handle(it) }
        })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_RECORD_AUDIO_PERMISSION -> {
                permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                init()
            }
        }
        if (!permissionToRecordAccepted) finish()
    }

    private fun init() {
        recorder.init()
        stopButton.setOnClickListener {
            onClickStop()
        }
    }

    fun onClickStop() {
        viewStateStore.dismissRecording()
    }

    override fun onTimeUpdate(time: Long) {
        viewStateStore.updateRecordDuration(time)
    }

    private fun currentViewState(): RecorderViewState = viewStateStore.recorderViewState.value!!

    private fun handle(viewState: RecorderViewState) {
        when (viewState.action) {
            RecorderViewState.Action.UPDATE_RECORD_DURATION -> {
                recorderTime?.text = getTimeFormat(currentViewState().recordDuration)
            }
            RecorderViewState.Action.DISMISS_RECORDING -> {
                Log.d(LOG_TAG, "Stop button click")
                recorder.onStopRecord()
                setResult(RESULT_OK, Intent())
                finish()
            }
        }
    }

    companion object {
        val RECORDER_REQUEST_CODE = 763

        fun start(context: Context) {
            val intent = Intent(context, RecorderActivity::class.java)
            context.startActivity(intent)
        }

        fun getTimeFormat(time: Long): String {
            val secs = (time / 1000).toInt()
            val minutes = secs / 60
            val hours = minutes / 60
            return ("" + hours + ":" + String.format("%02d", minutes) + ":"
                    + String.format("%02d", secs % 60))
        }
    }
}
