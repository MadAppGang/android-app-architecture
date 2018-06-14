package com.madappgang.madappgangmvvmtestarch.application

import com.madappgang.madappgangmvvmtestarch.model.models.SourceFile
import com.madappgang.madappgangmvvmtestarch.model.repos.RecordingRepository

/**
 * Created by Serhii Chaban sc@madappgang.com on 13.06.18.
 */
class RecordingRepositoryMock(private val filesCount:Int= 10) : RecordingRepository {
    override fun get(folder: String, id: String): SourceFile {
        return generateMockFiles(folder).first { it.id == id }
    }

    override fun get(portion: RecordingRepository.DataPortion): List<SourceFile> {
        return generateMockFiles(folder = portion.folder)
    }

    private fun generateMockFiles(folder: String) =
            (1..filesCount).map { SourceFile("file$it.mp4", "file$it.mp4", "/$folder", false) }
}