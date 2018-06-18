package com.madappgang.madappgangmvvmtestarch

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.Observer
import com.madappgang.madappgangmvvmtestarch.model.models.SourceFile
import com.madappgang.madappgangmvvmtestarch.model.repos.RecordingRepository
import com.madappgang.madappgangmvvmtestarch.model.repos.RecordingRepositoryMock
import com.madappgang.madappgangmvvmtestarch.model.useCases.GetRecordingsUseCase
import com.madappgang.madappgangmvvmtestarch.ui.recordings.RecordingsViewModel
import kotlinx.coroutines.experimental.Unconfined
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(MockitoJUnitRunner::class)
class RecordingsViewModelUnitTests {
    val filesCount = 10
    private val repository: RecordingRepository = RecordingRepositoryMock(filesCount)
    private val folderPath = "defaultFolder"
    private val usecase = GetRecordingsUseCase(repository)
    private val context = Unconfined
    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    @Mock
    lateinit var observer: Observer<List<SourceFile>>

    @Test
    fun `enshure files didn't filtered`() {
        val configurator = RecordingsViewModel.Configurator(folderPath, context, context, usecase)
        val recordingsViewModel = RecordingsViewModel(configurator)
        recordingsViewModel.recordings.observeForever(observer)
        recordingsViewModel.updateRecordingsList()
        assert(recordingsViewModel.recordings.value!!.size == filesCount)
    }
}
