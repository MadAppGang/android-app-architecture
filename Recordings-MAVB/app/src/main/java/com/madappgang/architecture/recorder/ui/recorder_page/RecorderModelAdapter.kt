package com.madappgang.architecture.recorder.ui.recorder_page

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer

/**
 * Created by Bohdan Shchavinskiy <bogdan@madappgang.com> on 10.09.2018.
 */
class RecorderModelAdapter(owner: LifecycleOwner, private val recorderViewBinderCallback: RecorderViewBinderCallback) {

    private val recorderModel = RecorderModel()

    init {
        recorderModel.getViewState()?.observe(owner, Observer<RecorderViewState> {
            it?.let { handle(it) }
        })
    }

    private fun handle(viewState: RecorderViewState) {
        when (viewState.action) {
            RecorderViewState.Action.UPDATE_RECORD_DURATION -> {
                recorderViewBinderCallback.updateRecordDuration(viewState.recordDuration)
            }
        }
    }

    fun onStopRecord() {
        recorderModel.onStopRecord()
    }
}