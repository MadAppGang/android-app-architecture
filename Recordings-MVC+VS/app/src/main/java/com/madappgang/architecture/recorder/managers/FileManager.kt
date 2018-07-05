package com.madappgang.architecture.recorder.managers

import com.madappgang.architecture.recorder.data.models.FileModel
import com.madappgang.architecture.recorder.data.storages.FileStorage
import com.madappgang.architecture.recorder.data.storages.FileStorage.Companion.mainDirectory

/**
 * Created by Bohdan Shchavinskiy <bogdan@madappgang.com> on 12.06.2018.
 */
class FileManager(private val fileStorage: FileStorage) {

    init {
        fileStorage.saveDirectory(FileModel(mainDirectory))
    }

    fun onSaveFolder(path: String, name: String, callback: FileManagerCallback) {
        fileStorage.saveDirectory(FileModel(path, name, true))
        callback.onResult()
    }

    fun onSaveRecord(path: String, name: String, callback: FileManagerCallback) {
        if (fileStorage.saveRecord(FileModel(path, name))) callback.onResult()
    }

    fun getListFiles(currentPath: String): MutableList<FileModel> {
        return fileStorage.getListFiles(currentPath)
    }

    fun removeFile(file: FileModel) {
        fileStorage.deleteFile(file)
    }

    fun renameFile(oldFilePath: String, newFilePath: String): Boolean {
        return fileStorage.renameFile(FileModel(oldFilePath), FileModel(newFilePath))
    }

    fun getFileNameByPath(currentPath: String): String {
        return fileStorage.getFileNameByPath(currentPath)
    }

    interface FileManagerCallback {
        fun onResult()
    }
}