package com.madappgang.madappgangmvvmtestarch.model.useCases

import com.jakewharton.rxrelay2.BehaviorRelay
import com.madappgang.madappgangmvvmtestarch.model.models.SourceFile
import com.madappgang.madappgangmvvmtestarch.model.repos.RecordingRepository
import com.madappgang.madappgangmvvmtestarch.model.repos.RecordingRepository.DataPortion.AllData
import com.madappgang.madappgangmvvmtestarch.model.repos.RecordingRepositoryImpl
import com.madappgang.madappgangmvvmtestarch.utils.Result
import io.reactivex.Observable
import io.reactivex.Single
import org.kodein.di.Kodein
import org.kodein.di.generic.instance

/**
 * Created by Serhii Chaban sc@madappgang.com on 29.05.18.
 */
class GetRecordingsUseCase(private val recordingRepository: RecordingRepository) {
    val recordings = BehaviorRelay.create<Result<List<SourceFile>, Throwable>>()
    operator fun invoke(folderPath: String) {
        return try {
            val value = recordingRepository.get(AllData(folderPath))
            recordings.accept(Result.Success(value))
        } catch (e: Throwable) {
            recordings.accept(Result.Error(e))
        }
    }
}