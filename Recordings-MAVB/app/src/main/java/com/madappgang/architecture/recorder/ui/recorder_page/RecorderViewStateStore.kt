package com.madappgang.architecture.recorder.ui.recorder_page

import android.arch.lifecycle.MutableLiveData

/**
 * Created by Bohdan Shchavinskiy <bogdan@madappgang.com> on 05.07.2018.
 */
class RecorderViewStateStore {

    var recorderView: RecorderViewState? = RecorderViewState()
    var recorderViewState: MutableLiveData<RecorderViewState>? = MutableLiveData()

    init {
        recorderViewState?.value = recorderView
    }

    fun updateRecordDuration(recordDuration: Long) {
        recorderView = recorderView?.copy(action = RecorderViewState.Action.UPDATE_RECORD_DURATION, recordDuration = recordDuration)
        recorderViewState?.value = recorderView
    }

    fun clearData() {
        recorderView = null
        recorderViewState = null
    }
}