package com.madappgang.madappgangmvvmtestarch

import com.madappgang.madappgangmvvmtestarch.model.models.SourceFile
import com.madappgang.madappgangmvvmtestarch.model.repos.RecordingRepository
import com.madappgang.madappgangmvvmtestarch.model.repos.RecordingRepositoryMock
import com.madappgang.madappgangmvvmtestarch.model.useCases.GetRecordingUseCase
import com.madappgang.madappgangmvvmtestarch.utils.Result
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class GetRecordingUnitTests {
    val filesCount = 10
    val repository: RecordingRepository = RecordingRepositoryMock(filesCount)
    @Test

    fun `get file by id, if file exist`() {
        val id = "file1.mp4"
        val recordingUseCase = GetRecordingUseCase(repository)
        val result = recordingUseCase[id]
        assert(result is Result.Success)
        assertEquals(id, (result as Result.Success<SourceFile>).value.id)
    }

    @Test
    fun `get file by id, if file not exist`() {
        val id = "file20.mp4"
        val recordingUseCase = GetRecordingUseCase(repository)
        val result = recordingUseCase[id]
        assert(result is Result.Error)
        assert((result as Result.Error<Throwable>).error is NoSuchElementException)
    }
}
