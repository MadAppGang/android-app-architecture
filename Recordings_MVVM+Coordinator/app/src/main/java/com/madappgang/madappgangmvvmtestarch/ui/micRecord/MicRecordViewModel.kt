package com.madappgang.madappgangmvvmtestarch.ui.micRecord

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel;
import com.madappgang.madappgangmvvmtestarch.model.service.PlayerService
import com.madappgang.madappgangmvvmtestarch.model.useCases.RecordDataUseCase
import com.madappgang.madappgangmvvmtestarch.utils.ActionLiveData
import kotlinx.coroutines.experimental.CoroutineDispatcher
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch

class MicRecordViewModel(configurator: Configurator) : ViewModel() {
    class Configurator(
            val playerService: PlayerService,
            val recordDataUseCase: RecordDataUseCase,
            val uiContext: CoroutineDispatcher,
            val bgContext: CoroutineDispatcher)

    enum class RecorderState {
        record,
        play,
        pause
    }

    val uiContext: CoroutineDispatcher = configurator.uiContext
    val bgContext: CoroutineDispatcher = configurator.bgContext
    val recorder: RecordDataUseCase = configurator.recordDataUseCase
    val playerService: PlayerService = configurator.playerService

    var progressUpdater: Job = Job()
    private var job: Job = Job()

    private fun updateProgress() = launch(parent = job, context = bgContext) {
        while (true) {
            val delayInterval = 1000
            readTimeMillis.postValue((readTimeMillis.value ?: 0)+ delayInterval)
            delay(delayInterval)
        }
    }

    val saveAction: ActionLiveData<Unit> = ActionLiveData()
    val readTimeMillis: MutableLiveData<Int> = MutableLiveData()
    val recordState: MutableLiveData<RecorderState> = MutableLiveData()

    init {
        readTimeMillis.postValue(0)
    }

    private fun startProgressUpdater() {
        progressUpdater = updateProgress()
    }

    private fun stopProgressUpdater() {
        progressUpdater.cancel()
    }

    fun startPauseRecord() {

        if (recordState.value != RecorderState.record) {
            recorder.startRecord()
            recordState.postValue(RecorderState.record)
            startProgressUpdater()
        } else {
            recordState.postValue(RecorderState.pause)
            recorder.stopRecord()
            stopProgressUpdater()
        }
    }

    fun stopRecord() {
        recorder.stopRecord()
        saveAction.postValue(Unit)
    }

    fun playAvailableRecord() {

    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
        recorder.stopRecord()
        recorder.releaseData()
    }

    fun saveWithName(string: String) {
        recorder.saveRecordWithName(string)
    }
}
