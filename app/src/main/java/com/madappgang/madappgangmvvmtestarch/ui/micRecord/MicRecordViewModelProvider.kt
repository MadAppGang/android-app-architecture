package com.madappgang.madappgangmvvmtestarch.ui.micRecord

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import org.kodein.di.Kodein
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import com.madappgang.madappgangmvvmtestarch.model.useCases.GetRecording
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
            val getRecordingsService: GetRecording  by kodein.instance()
            val configurator = MicRecordViewModel.Configurator( uiContext, bgContext)
            return MicRecordViewModel(configurator) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")

    }

}