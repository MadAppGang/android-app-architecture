package com.madappgang.architecture.recorder.ui.recorder_page

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.madappgang.architecture.recorder.R
import kotlinx.android.synthetic.main.activity_recorder.*


class RecorderActivity : AppCompatActivity() {

    private val LOG_TAG = "RecorderActivity"
    private val recorderViewBinder = RecorderViewBinder(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recorder)
        prepareViewBinder()
        init()
    }

    private fun prepareViewBinder() {
        recorderViewBinder.updateRecordTime = { updateRecordTime(it) }
    }

    private fun init() {
        stopButton.setOnClickListener {
            onClickStop()
        }
    }

    fun onClickStop() {
        Log.d(LOG_TAG, "Stop button click")
        recorderViewBinder.onStopRecord()
        setResult(RESULT_OK, Intent())
        finish()
    }

    private fun updateRecordTime(time: Long) {
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
