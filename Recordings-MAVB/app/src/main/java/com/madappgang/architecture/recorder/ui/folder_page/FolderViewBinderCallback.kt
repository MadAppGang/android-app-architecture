package com.madappgang.architecture.recorder.ui.folder_page

import com.madappgang.architecture.recorder.data.models.FileModel

/**
 * Created by Bohdan Shchavinskiy <bogdan@madappgang.com> on 05.09.2018.
 */
interface FolderViewBinderCallback {

    fun showDialog(isFolderDialog: Boolean, viewStateEditing: Boolean)
    fun setFolderTitle(filePath: String, fileName: String?)
    fun toggleEditing(viewStateEditing: Boolean)
    fun initPage(file: FileModel)
    fun onCreatePlayerViewState(filePath: String)
    fun onCreateRecorderViewState()
    fun updateListFiles()
}