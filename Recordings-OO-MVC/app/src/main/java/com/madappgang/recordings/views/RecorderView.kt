/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 6/13/18.
 */

package com.madappgang.recordings.layoutmanagers

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.TextView
import com.madappgang.recordings.R
import com.madappgang.recordings.extensions.formatMilliseconds
import com.madappgang.recordings.kit.Recorder
import kotlinx.android.synthetic.main.view_recorder.view.*


internal class RecorderView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    var onStartRecording: () -> Unit = {}
    var onStopRecording: () -> Unit = {}
    var onPauseResumeRecording: () -> Unit = {}

    private val time by lazy { findViewById<TextView>(R.id.time) }

    init {
        inflate(getContext(), R.layout.view_recorder, this)

        stopRecording.setOnClickListener { onStopRecording.invoke() }
        startRecording.setOnClickListener { onStartRecording.invoke() }
        pauseResumeRecording.setOnClickListener { onPauseResumeRecording.invoke() }
    }

    fun setStatus(status: Recorder.Status) {
        updateButton(status)
    }

    fun setTime(millisecond: Int) {
        time.text = time.formatMilliseconds(millisecond)
    }

    private fun updateButton(status: Recorder.Status) {
        when (status) {
            Recorder.Status.NOT_STARTED -> applyNotStartedStateForButton()
            Recorder.Status.STARTED -> applyStartedStateForButton()
            Recorder.Status.PAUSED -> applyPausedStateForButton()
            Recorder.Status.COMPLETED -> applyCompletedStateForButton()
        }
    }

    private fun applyNotStartedStateForButton() {
        startRecording.isEnabled = true
        pauseResumeRecording.isEnabled = false
        pauseResumeRecording.text = context.getString(R.string.RecorderActivity_pause)
        stopRecording.isEnabled = false
    }

    private fun applyStartedStateForButton() {
        startRecording.isEnabled = false
        pauseResumeRecording.isEnabled = true
        pauseResumeRecording.text = context.getString(R.string.RecorderActivity_pause)
        stopRecording.isEnabled = true
    }

    private fun applyPausedStateForButton() {
        startRecording.isEnabled = false
        pauseResumeRecording.isEnabled = true
        pauseResumeRecording.text = context.getString(R.string.RecorderActivity_resume)
        stopRecording.isEnabled = true
    }

    private fun applyCompletedStateForButton() {
        startRecording.isEnabled = false
        pauseResumeRecording.isEnabled = false
        pauseResumeRecording.text = context.getString(R.string.RecorderActivity_pause)
        stopRecording.isEnabled = false
    }
}