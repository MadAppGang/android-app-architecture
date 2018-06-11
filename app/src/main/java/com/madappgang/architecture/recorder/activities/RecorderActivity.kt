package com.madappgang.architecture.recorder.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.madappgang.architecture.recorder.R
import com.madappgang.architecture.recorder.helpers.Recorder
import kotlinx.android.synthetic.main.activity_recorder.*


class RecorderActivity : AppCompatActivity() {

    private val LOG_TAG = "RecorderActivity"
    private val REQUEST_RECORD_AUDIO_PERMISSION = 200
    private val permissions = arrayOf<String>(Manifest.permission.RECORD_AUDIO)
    private val recorder = Recorder(this)
    private var permissionToRecordAccepted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recorder)

        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION)
        stopButton.setOnClickListener { recorder.onStopRecord() }
        recorder.onStartRecord()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_RECORD_AUDIO_PERMISSION -> permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
        }
        if (!permissionToRecordAccepted) finish()
    }

    fun onClickStop(v: View) {
        Log.d("Stop button", "stop")
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, RecorderActivity::class.java)
            context.startActivity(intent)
        }
    }
}
