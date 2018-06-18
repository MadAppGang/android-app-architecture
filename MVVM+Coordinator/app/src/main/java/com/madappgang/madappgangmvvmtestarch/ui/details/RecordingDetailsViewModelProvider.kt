package com.madappgang.madappgangmvvmtestarch.ui.details

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.madappgang.madappgangmvvmtestarch.model.service.PlayerService
import org.kodein.di.Kodein
import com.madappgang.madappgangmvvmtestarch.model.useCases.GetRecordingUseCase
import kotlinx.coroutines.experimental.CoroutineDispatcher
import org.kodein.di.generic.instance


/**
 * Created by Serhii Chaban sc@madappgang.com on 06.06.18.
 */
class RecordingDetailsViewModelProvider(val kodein: Kodein, val fileId: String) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecordDetailsViewModel::class.java)) {
            val uiContext: CoroutineDispatcher by kodein.instance("uiContext")
            val bgContext: CoroutineDispatcher  by kodein.instance("bgContext")
            val getRecordingsService: GetRecordingUseCase  by kodein.instance()
            val playerService: PlayerService  by kodein.instance()
            val configurator = RecordDetailsViewModel.Configurator(fileId, uiContext, bgContext, getRecordingsService, playerService)
            return RecordDetailsViewModel(configurator) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")

    }

}