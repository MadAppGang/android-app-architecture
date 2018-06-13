package com.madappgang.madappgangmvvmtestarch.model.useCases

import com.madappgang.madappgangmvvmtestarch.model.models.SourceFile
import com.madappgang.madappgangmvvmtestarch.model.repos.RecordingRepository
import com.madappgang.madappgangmvvmtestarch.model.repos.RecordingRepository.DataPortion.AllData
import com.madappgang.madappgangmvvmtestarch.model.repos.RecordingRepositoryImpl
import com.madappgang.madappgangmvvmtestarch.utils.Result
import org.kodein.di.Kodein
import org.kodein.di.generic.instance

/**
 * Created by Serhii Chaban sc@madappgang.com on 29.05.18.
 */
class GetRecordingsUseCase(private val recordingRepository: RecordingRepository) {

    operator fun invoke(folderPath: String): Result<List<SourceFile>, Throwable> {
        return try {
            val value = recordingRepository.get(AllData(folderPath))
            Result.Success(value)
        } catch (e: Throwable) {
            Result.Error(e)
        }
    }
}