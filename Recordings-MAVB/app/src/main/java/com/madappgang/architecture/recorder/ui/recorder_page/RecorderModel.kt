package com.madappgang.architecture.recorder.ui.recorder_page

import android.arch.lifecycle.MutableLiveData
import com.madappgang.architecture.recorder.application.AppInstance
import com.madappgang.architecture.recorder.data.repositories.RecordingRepository

/**
 * Created by Bohdan Shchavinskiy <bogdan@madappgang.com> on 10.09.2018.
 */
class RecorderModel : RecordingRepository.RecordTimeUpdate {
    override fun onTimeUpdate(time: Long) {
        recorderViewStateStore?.updateRecordDuration(time)
    }

    private val recorder = AppInstance.managersInstance.recorder
    private val viewStateStore = AppInstance.managersInstance.viewStateStore
    private val recorderViewStateStore = viewStateStore.recorderViewStateStore?.value

    init {
        recorder.init(this)
    }

    fun onStopRecord() {
        recorder.onStopRecord()
    }

    private fun currentViewState(): RecorderViewState = recorderViewStateStore?.recorderView
            ?: RecorderViewState()

    fun getViewState(): MutableLiveData<RecorderViewState>? {
        return recorderViewStateStore?.recorderViewState
    }
}