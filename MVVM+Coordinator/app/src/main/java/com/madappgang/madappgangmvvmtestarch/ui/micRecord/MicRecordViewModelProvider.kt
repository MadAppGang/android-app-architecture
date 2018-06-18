package com.madappgang.madappgangmvvmtestarch.ui.micRecord

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.madappgang.madappgangmvvmtestarch.model.service.PlayerService
import org.kodein.di.Kodein
import com.madappgang.madappgangmvvmtestarch.model.useCases.RecordDataUseCase
import kotlinx.coroutines.experimental.CoroutineDispatcher
import org.kodein.di.generic.instance


/**
 * Created by Serhii Chaban sc@madappgang.com on 06.06.18.
 */
class MicRecordViewModelProvider(val kodein: Kodein) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MicRecordViewModel::class.java)) {
            val uiContext: CoroutineDispatcher by kodein.instance("uiContext")
            val bgContext: CoroutineDispatcher  by kodein.instance("bgContext")
            val recordDataUseCase: RecordDataUseCase  by kodein.instance()
            val playerService: PlayerService  by kodein.instance()
            val configurator = MicRecordViewModel.Configurator(playerService, recordDataUseCase, uiContext, bgContext)
            return MicRecordViewModel(configurator) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")

    }

}