package com.madappgang.madappgangmvvmtestarch.model.repos

import com.madappgang.madappgangmvvmtestarch.model.models.SourceFile
import com.madappgang.madappgangmvvmtestarch.model.repos.RecordingRepository.DataPortion.AllData
import com.madappgang.madappgangmvvmtestarch.model.repos.RecordingRepository.DataPortion.SingleFile
import java.io.File

class RecordingRepositoryImpl : RecordingRepository {

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
            is SingleFile -> {
                val listFiles = folder.listFiles().filter { it.name == portion.id }.mapNotNull { sourceOrNull(it) }
                listFiles
            }
        }
    }

    private fun sourceOrNull(it: File) =
            SourceFile(it.name, it.name, it.parentFile.absolutePath, it.isDirectory)

}