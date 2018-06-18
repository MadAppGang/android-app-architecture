package com.madappgang.madappgangmvvmtestarch.model.repos

import com.madappgang.madappgangmvvmtestarch.model.models.SourceFile
import com.madappgang.madappgangmvvmtestarch.model.repos.RecordingRepository.DataPortion.AllData
import java.io.File

class RecordingRepositoryImpl : RecordingRepository {
    override fun get(id: String): SourceFile {
        val folder = File(id).parentFile
        return folder.listFiles().filter { it.absolutePath.contains(id) }.mapNotNull { sourceOrNull(it) }.first { it.id == id }
    }

    override fun get(portion: RecordingRepository.DataPortion): List<SourceFile> {
        val folder = File(portion.folder)
        if (!folder.exists()) {
            folder.mkdirs()
        }
        return when (portion) {
            is AllData -> {
                val listFiles = folder.listFiles().mapNotNull { sourceOrNull(it) }
                listFiles
            }
        }
    }

    private fun sourceOrNull(it: File) =
            SourceFile(it.absolutePath, it.name, it.parentFile.absolutePath, it.isDirectory)

}