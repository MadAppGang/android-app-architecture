package com.madappgang.madappgangmvvmtestarch.ui.details

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.madappgang.madappgangmvvmtestarch.model.service.PlayerService
import com.madappgang.madappgangmvvmtestarch.model.useCases.GetRecordingUseCase
import com.madappgang.madappgangmvvmtestarch.utils.Result.Error
import com.madappgang.madappgangmvvmtestarch.utils.Result.Success
import kotlinx.coroutines.experimental.*

class RecordDetailsViewModel(configurator: Configurator) : ViewModel() {
    class Configurator(
            val recordId: String,
            val uiContext: CoroutineDispatcher,
            val bgContext: CoroutineDispatcher,
            val getRecordingService: GetRecordingUseCase,
            val mediaPlayer: PlayerService
    )

    enum class PlayerState {
        play,
        pause
    }


    private val tenSec = 1000 * 10

    private val uiContext: CoroutineDispatcher = configurator.uiContext
    private val bgContext: CoroutineDispatcher = configurator.bgContext
    private val recording: GetRecordingUseCase = configurator.getRecordingService
    private val mediaPlayer: PlayerService = configurator.mediaPlayer
    private val recordId: String = configurator.recordId

    private var job: Job? = Job()
    private var loadingContentJob: Job? = null

    val fileName = MutableLiveData<String>()
    val progress = MutableLiveData<Int>()
    val maxProgress = MutableLiveData<Int>()
    val state = MutableLiveData<PlayerState>()

    init {
        mediaPlayer.onInit = {
            maxProgress.postValue(mediaPlayer.duration)
            state.postValue(RecordDetailsViewModel.PlayerState.play)
            startProgressUpdater()
        }
        mediaPlayer.onComplete = {
            state.postValue(RecordDetailsViewModel.PlayerState.pause)
        }
        mediaPlayer.onError = { error, message ->
            if (error == PlayerService.ErrorCodes().sourceDeletedError) {
                mediaPlayer.seekTo(0)
                mediaPlayer.pause()
                true
            }
            false
        }
    }

    private fun startProgressUpdater() {
        progressUpdater = updateProgress()
    }

    private fun stopProgressUpdater() {
        progressUpdater.cancel()
    }

    var progressUpdater: Job = Job()

    private fun updateProgress() = launch(parent = job, context = bgContext) {
        while (true) {
            progress.postValue(mediaPlayer.currentPosition)
            delay(1000)
        }
    }

    fun startPlayRecord() {
        loadingContentJob?.cancel()
        loadingContentJob = launch(parent = job, context = uiContext) {
            val result = async(parent = job, context = uiContext) { recording[recordId] }.await()
            when (result) {is Success -> {
                val sourceFile = result.value
                val filepath = "${sourceFile?.filePath}/${sourceFile?.name}"
                fileName.postValue(sourceFile?.name)
                mediaPlayer.setDataSource(filepath)
                mediaPlayer.startPlay()
            }
                is Error -> {

                }
            }

        }
    }

    fun onPausePressed() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            state.postValue(PlayerState.pause)
        } else {
            mediaPlayer.start()
            state.postValue(PlayerState.play)
        }
    }

    fun seekPosition(position: Int, isUserAction: Boolean) {
        if (isUserAction) {
            mediaPlayer.seekTo(position)
        }
    }

    fun seekForward() {
        seekPosition(mediaPlayer.currentPosition + tenSec, true)
    }

    fun seekBackward() {
        seekPosition(mediaPlayer.currentPosition - tenSec, true)
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
        mediaPlayer.stop()
        mediaPlayer.reset()
        mediaPlayer.release()
    }
}
