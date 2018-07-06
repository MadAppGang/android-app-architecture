package com.madappgang.architecture.recorder.ui.recorder_page

/**
 * Created by Bohdan Shchavinskiy <bogdan@madappgang.com> on 05.07.2018.
 */
data class RecorderViewState(val recordDuration: Long = 0, val action: RecorderViewState.Action? = null) {
    enum class Action {
        UPDATE_RECORD_DURATION,
        DISMISS_RECORDING
    }
}