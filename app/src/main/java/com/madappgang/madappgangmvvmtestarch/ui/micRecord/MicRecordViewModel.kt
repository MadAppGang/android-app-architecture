package com.madappgang.madappgangmvvmtestarch.ui.micRecord

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel;
import com.madappgang.madappgangmvvmtestarch.utils.ActionLiveData
import kotlinx.coroutines.experimental.CoroutineDispatcher

class MicRecordViewModel(configurator: Configurator) : ViewModel() {
    class Configurator(
            val uiContext: CoroutineDispatcher,
            val bgContext: CoroutineDispatcher)

    enum class RecorderState {
        record,
        play,
        pause
    }

    val uiContext: CoroutineDispatcher = configurator.uiContext
    val bgContext: CoroutineDispatcher = configurator.bgContext

    val saveAction: LiveData<Unit> = ActionLiveData()
    val readTimeMillis: MutableLiveData<Int> = MutableLiveData()
    val recordState: MutableLiveData<RecorderState> = MutableLiveData()
    val isPlayAvailable: MutableLiveData<Boolean> = MutableLiveData()

    fun startPauseRecord() {

    }

    fun stopRecord() {

    }

    fun playAvailableRecord() {

    }
}
