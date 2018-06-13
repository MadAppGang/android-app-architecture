package com.madappgang.architecture.recorder.activities

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.madappgang.architecture.recorder.AppInstance
import com.madappgang.architecture.recorder.R
import com.madappgang.architecture.recorder.helpers.Recorder
import com.madappgang.architecture.recorder.view_state_model.RecorderViewState
import kotlinx.android.synthetic.main.activity_recorder.*


class RecorderActivity : AppCompatActivity(), Recorder.RecordTimeUpdate {

    private val LOG_TAG = "RecorderActivity"
    private val recorder = AppInstance.appInstance.recorder
    private val viewStateStore = AppInstance.appInstance.viewStateStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recorder)
        init()

        viewStateStore.recorderViewState.observe(this, Observer<RecorderViewState> {
            it?.let { handle(it) }
        })
    }

    private fun init() {
        recorder.init(this)
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
        viewStateStore.updateRecordDuration(time)
    }

    private fun currentViewState(): RecorderViewState = viewStateStore.recorderViewState.value!!

    private fun handle(viewState: RecorderViewState) {
        when (viewState.action) {
            RecorderViewState.Action.UPDATE_RECORD_DURATION -> {
                recorderTime?.text = getTimeFormat(currentViewState().recordDuration)
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
