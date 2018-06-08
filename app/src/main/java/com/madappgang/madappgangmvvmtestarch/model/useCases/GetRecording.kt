package com.madappgang.madappgangmvvmtestarch.model.useCases

import com.madappgang.madappgangmvvmtestarch.model.models.SourceFile
import com.madappgang.madappgangmvvmtestarch.model.repos.RecordingRepository
import com.madappgang.madappgangmvvmtestarch.model.repos.RecordingRepository.DataPortion.SingleFile
import com.madappgang.madappgangmvvmtestarch.utils.Result
import org.kodein.di.Kodein
import org.kodein.di.generic.instance

/**
 * Created by Serhii Chaban sc@madappgang.com on 29.05.18.
 */
class GetRecording(val recordingRepository: RecordingRepository) {
    operator fun get(folder: String, id: String): Result<SourceFile?, Throwable> {
        return try {
            val sourceFile = recordingRepository[SingleFile(folder, id)]
            Result.Success(sourceFile[0])
        } catch (e: Throwable) {
            Result.Error(e)
        }
    }
}