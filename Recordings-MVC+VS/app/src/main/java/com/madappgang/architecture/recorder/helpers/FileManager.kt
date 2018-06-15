package com.madappgang.architecture.recorder.helpers

import android.os.Environment
import java.io.File

/**
 * Created by Bohdan Shchavinskiy <bogdan@madappgang.com> on 12.06.2018.
 */
class FileManager {

    init {
        File(mainDirectory).mkdirs()
    }

    fun onSaveFolder(path: String, name: String, callback: FileManagerCallback) {
        File(path, name).mkdirs()
        callback.onResult()
    }

    fun onSaveRecord(path: String, name: String, callback: FileManagerCallback) {
        val cacheRecord = File(mainDirectory, "$testRecordName$recordFormat")
        if (cacheRecord.absoluteFile.exists()) {
            cacheRecord.copyTo(File(path, "$name$recordFormat"), true, DEFAULT_BUFFER_SIZE)
            callback.onResult()
        }
    }

    fun getListFiles(currentPath: String): MutableList<File> {
        val files = mutableListOf<File>()
        val parentDir = File(currentPath)
        parentDir.listFiles()?.forEach { if (it.name != (testRecordName + recordFormat)) files.add(it) }
        return files
    }

    fun removeFile(file: File) {
        if (file.isDirectory) file.list().forEach { File(file, it).delete() }
        file.delete()
    }

    companion object {
        val mainDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).absolutePath + "/Records"
        val testRecordName = "audioRecordTest"
        val recordFormat = ".3gp"
    }

    interface FileManagerCallback {
        fun onResult()
    }

    fun renameFile(oldFilePath: String, newFilePath: String): Boolean {
        val oldFile = File(oldFilePath)
        val newFile = File(newFilePath)
        val result = oldFile.renameTo(newFile)
        return result
    }
}