package com.madappgang.architecture.recorder.data.storages

import android.os.Environment
import com.madappgang.architecture.recorder.data.models.FileModel

/**
 * Created by Bohdan Shchavinskiy <bogdan@madappgang.com> on 04.07.2018.
 */
interface FileStorage {
    fun saveDirectory(fileModel: FileModel)
    fun saveRecord(fileModel: FileModel): Boolean
    fun getListFiles(currentPath: String): MutableList<FileModel>
    fun deleteFile(fileModel: FileModel)
    fun renameFile(oldFileModel: FileModel, newFileModel: FileModel) : Boolean
    fun getFileNameByPath(currentPath: String): String


    companion object {
        val mainDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).absolutePath + "/Records"
        const val testRecordName = "audioRecordTest"
        const val recordFormat = ".3gp"
    }
}