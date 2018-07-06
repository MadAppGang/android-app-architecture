package com.madappgang.architecture.recorder.data.storages

import com.madappgang.architecture.recorder.data.models.FileModel
import com.madappgang.architecture.recorder.data.storages.FileStorage.Companion.recordFormat
import com.madappgang.architecture.recorder.data.storages.FileStorage.Companion.testRecordName
import java.io.File

/**
 * Created by Bohdan Shchavinskiy <bogdan@madappgang.com> on 04.07.2018.
 */
class FileStorageImpl : FileStorage {
    override fun getFileNameByPath(currentPath: String): String {
        return File(currentPath).name
    }

    override fun renameFile(oldFileModel: FileModel, newFileModel: FileModel): Boolean {
        val oldFile = File(oldFileModel.filePath)
        val newFile = File(newFileModel.filePath)
        val result = oldFile.renameTo(newFile)
        return result
    }

    override fun deleteFile(fileModel: FileModel) {
        val file = File(fileModel.filePath)
        if (file.isDirectory) file.list().forEach { File(file, it).delete() }
        file.delete()
    }

    override fun getListFiles(currentPath: String): MutableList<FileModel> {
        val files = mutableListOf<FileModel>()
        val parentDir = File(currentPath)
        parentDir.listFiles()?.forEach { if (it.name != (testRecordName + recordFormat)) files.add(FileModel(it.absolutePath, it.name, it.isDirectory)) }
        return files
    }

    override fun saveRecord(fileModel: FileModel): Boolean {
        val cacheRecord = File(FileStorage.mainDirectory, "$testRecordName$recordFormat")
        if (cacheRecord.absoluteFile.exists()) {
            cacheRecord.copyTo(File(fileModel.filePath, "${fileModel.name}$recordFormat"), true, DEFAULT_BUFFER_SIZE)
            return true
        }
        return false
    }

    override fun saveDirectory(fileModel: FileModel) {
        var filePath = fileModel.filePath
        filePath += if (fileModel.filePath.isNotEmpty()) "/${fileModel.name}" else ""
        File(filePath).mkdirs()
    }
}