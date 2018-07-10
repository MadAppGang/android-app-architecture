package com.madappgang.madappgangmvvmtestarch.ui.micRecord

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel;
import com.madappgang.madappgangmvvmtestarch.model.useCases.RecordDataUseCase
import com.madappgang.madappgangmvvmtestarch.ui.micRecord.MicRecordViewModel.RecorderState.*
import com.madappgang.madappgangmvvmtestarch.utils.ActionLiveData
import kotlinx.coroutines.experimental.CoroutineDispatcher
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch

class MicRecordViewModel(configurator: Configurator) : ViewModel() {
    class Configurator(
            val recordDataUseCase: RecordDataUseCase,
            val uiContext: CoroutineDispatcher,
            val bgContext: CoroutineDispatcher)

    enum class RecorderState {
        initial,
        recording,
        paused,
        stopped
    }

    val uiContext: CoroutineDispatcher = configurator.uiContext
    val bgContext: CoroutineDispatcher = configurator.bgContext
    val recorder: RecordDataUseCase = configurator.recordDataUseCase

    var progressUpdater: Job = Job()
    private var job: Job = Job()

    private fun updateProgress() = launch(parent = job, context = bgContext) {
        while (true) {
            val delayInterval = 1000
            readTimeMillis.postValue((readTimeMillis.value ?: 0) + delayInterval)
            delay(delayInterval)
        }
    }

    sealed class SaveAction() {
        class ShowSaveAction() : SaveAction()
        class SaveActionSuccess() : SaveAction()
    }

    val saveAction: ActionLiveData<SaveAction> = ActionLiveData()
    val readTimeMillis: MutableLiveData<Int> = MutableLiveData()
    val recordState: MutableLiveData<RecorderState> = MutableLiveData()

    init {
        readTimeMillis.postValue(0)
        recorder.recorderStateCallback = {
            when (it) {
                is RecordDataUseCase.RecorderState.Initial -> {
                    recordState.postValue(initial)
                }
                is RecordDataUseCase.RecorderState.Started -> {
                    recordState.postValue(recording)
                    startProgressUpdater()

                }
                is RecordDataUseCase.RecorderState.Paused -> {
                    recordState.postValue(paused)
                    stopProgressUpdater()
                }
                is RecordDataUseCase.RecorderState.Stopped -> {
                    saveAction.postValue(SaveAction.ShowSaveAction())
                    stopProgressUpdater()
                    recordState.postValue(stopped)
                }
                is RecordDataUseCase.RecorderState.Throuble.PlayerError -> {
                    recordState.postValue(stopped)
                }
                is RecordDataUseCase.RecorderState.Throuble.Error -> {
                    recordState.postValue(stopped)
                }
                is RecordDataUseCase.RecorderState.FileSaved -> {
                    recordState.postValue(stopped)
                    saveAction.postValue(SaveAction.SaveActionSuccess())
                }
            }
        }
    }

    private fun startProgressUpdater() {
        progressUpdater = updateProgress()
    }

    private fun stopProgressUpdater() {
        progressUpdater.cancel()
    }

    fun startPauseRecord() {
        if (recordState.value != recording) {
            recorder.startRecord()
        } else {
            recorder.pauseRecord()
        }
    }

    fun stopRecord() {
        when (recordState.value) {
            recording -> {
                recorder.pauseRecord()
                recorder.stopRecord()
            }
            paused -> {
                recorder.stopRecord()
            }
            else -> {
            }
        }
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
