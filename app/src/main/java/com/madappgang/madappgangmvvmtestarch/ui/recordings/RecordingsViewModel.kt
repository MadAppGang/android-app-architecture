package com.madappgang.madappgangmvvmtestarch.ui.recordings

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.madappgang.madappgangmvvmtestarch.model.models.SourceFile
import com.madappgang.madappgangmvvmtestarch.model.useCases.GetRecordings
import com.madappgang.madappgangmvvmtestarch.utils.ActionLiveData
import com.madappgang.madappgangmvvmtestarch.utils.Result
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.UI
import org.kodein.di.Kodein
import org.kodein.di.generic.instance

class RecordingsViewModel(configurator: Configurator) : ViewModel() {
    /**we use @class Configurator  to feed dependencies ->
    * for view model to be sure we are not dependent on different injection frameworks inside view model **/
    class Configurator(
            val folderPath: String,
            val uiContext: CoroutineDispatcher,
            val bgContext: CoroutineDispatcher,
            val getRecordingsService: GetRecordings) {
    }

    val recordings = MutableLiveData<List<SourceFile>>()
    val error = ActionLiveData<String?>()
    val onShowRecording = ActionLiveData<SourceFile>()
    val isInEditMode = MutableLiveData<Boolean>()
    val isLoading = MutableLiveData<Boolean>()
    var job: Job = Job()
    var loadingJob: Job? = null
    private val uiContext: CoroutineDispatcher = configurator.uiContext
    private val bgContext: CoroutineDispatcher = configurator.bgContext
    private val getRecordingsService: GetRecordings = configurator.getRecordingsService
    private val folderPath: String = configurator.folderPath

    fun updateRecordingsList() {
        loadingJob?.cancel()
        loadingJob = launch(parent = job, context = uiContext) {
            isLoading.postValue(true)
            val result = async(parent = loadingJob, context = bgContext) {
                getRecordingsService.invoke(folderPath)
            }.await()
            when (result) {
                is Result.Success -> {
                    recordings.postValue(result.value)
                }
                is Result.Error -> {
                    error.postValue(result.error.message ?: "")
                }
            }
            isLoading.postValue(false)
        }
    }

    fun onRecordClick(sourceFile: SourceFile) {
        onShowRecording.postValue(sourceFile)
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
}
