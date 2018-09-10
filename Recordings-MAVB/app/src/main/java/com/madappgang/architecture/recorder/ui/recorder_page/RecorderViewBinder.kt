package com.madappgang.architecture.recorder.ui.recorder_page

import android.arch.lifecycle.LifecycleOwner

/**
 * Created by Bohdan Shchavinskiy <bogdan@madappgang.com> on 10.09.2018.
 */
class RecorderViewBinder(owner: LifecycleOwner) : RecorderViewBinderCallback {

    override fun updateRecordDuration(time: Long) {
        updateRecordTime(time)
    }

    fun onStopRecord() {
        recorderModelAdapter.onStopRecord()
    }

    private val recorderModelAdapter = RecorderModelAdapter(owner, this)
    var updateRecordTime: (progress: Long) -> Unit = {}

}