package com.madappgang.architecture.recorder.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.madappgang.architecture.recorder.R
import com.madappgang.architecture.recorder.helpers.Recorder
import kotlinx.android.synthetic.main.activity_recorder.*


class RecorderActivity : AppCompatActivity(), Recorder.RecordTimeUpdate {

    private val LOG_TAG = "RecorderActivity"
    private val REQUEST_RECORD_AUDIO_PERMISSION = 200
    private val permissions = arrayOf<String>(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
    private var permissionToRecordAccepted = false
    private val recorder = Recorder(this, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recorder)
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION)
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
        Log.d(LOG_TAG, "Stop button click")
        recorder.onStopRecord()
        setResult(RESULT_OK, Intent())
        finish()
    }

    override fun onTimeUpdate(time: Long) {
        recorderTime?.text = getTimeFormat(time)
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
