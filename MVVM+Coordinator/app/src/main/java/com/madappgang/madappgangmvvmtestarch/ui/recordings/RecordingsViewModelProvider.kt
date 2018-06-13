package com.madappgang.madappgangmvvmtestarch.ui.recordings

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import org.kodein.di.Kodein
import com.madappgang.madappgangmvvmtestarch.model.useCases.GetRecordingsUseCase
import kotlinx.coroutines.experimental.CoroutineDispatcher
import org.kodein.di.generic.instance


/**
 * Created by Serhii Chaban sc@madappgang.com on 06.06.18.
 */
class RecordingsViewModelProvider(val kodein: Kodein, val filePath: String) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecordingsViewModel::class.java)) {
            val uiContext: CoroutineDispatcher by kodein.instance("uiContext")
            val bgContext: CoroutineDispatcher  by kodein.instance("bgContext")
            val getRecordingsUseCaseService: GetRecordingsUseCase  by kodein.instance()
            val configurator = RecordingsViewModel.Configurator(filePath,uiContext, bgContext, getRecordingsUseCaseService)
            return RecordingsViewModel(configurator) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")

    }

}