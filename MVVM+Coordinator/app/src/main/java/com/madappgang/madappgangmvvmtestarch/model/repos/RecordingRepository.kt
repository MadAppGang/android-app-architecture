package com.madappgang.madappgangmvvmtestarch.model.repos

import com.madappgang.madappgangmvvmtestarch.model.models.SourceFile

/**
 * Created by Serhii Chaban sc@madappgang.com on 30.05.18.
 */
interface RecordingRepository {
    sealed class DataPortion(val folder: String) {
        class AllData(folder: String) : DataPortion(folder)
    }

    fun get(portion: DataPortion): List<SourceFile>
    fun get(id: String): SourceFile
}