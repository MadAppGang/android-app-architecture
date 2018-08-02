package com.madappgang.architecture.recorder.managers

import com.madappgang.architecture.recorder.data.repositories.RecordingRepository
import com.madappgang.architecture.recorder.data.services.PlayerService
import com.madappgang.architecture.recorder.data.storages.FileStorage
import com.madappgang.architecture.recorder.ui.folder_page.FolderViewBinder

/**
 * Created by Bohdan Shchavinskiy <bogdan@madappgang.com> on 05.07.2018.
 */
interface Managers {
    fun fileManager(): FileManager
    fun folderViewBinder(): FolderViewBinder
    fun viewStateStore(): ViewStateStoresManager
    fun recordingRepository(): RecordingRepository
    fun storageProvider(): FileStorage
    fun playerService(): PlayerService
}