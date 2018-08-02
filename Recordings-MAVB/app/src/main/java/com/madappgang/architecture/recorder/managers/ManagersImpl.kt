package com.madappgang.architecture.recorder.managers

import com.madappgang.architecture.recorder.data.repositories.RecordingRepository
import com.madappgang.architecture.recorder.data.repositories.RecordingRepositoryImpl
import com.madappgang.architecture.recorder.data.services.PlayerService
import com.madappgang.architecture.recorder.data.services.PlayerServiceImpl
import com.madappgang.architecture.recorder.data.storages.FileStorage
import com.madappgang.architecture.recorder.data.storages.FileStorageImpl
import com.madappgang.architecture.recorder.ui.folder_page.FolderViewBinder

/**
 * Created by Bohdan Shchavinskiy <bogdan@madappgang.com> on 05.07.2018.
 */
class ManagersImpl : Managers {
    override fun fileManager(): FileManager = FileManager(storageProvider())
    override fun folderViewBinder(): FolderViewBinder = FolderViewBinder()
    override fun viewStateStore(): ViewStateStoresManager = ViewStateStoresManager()
    override fun recordingRepository(): RecordingRepository = RecordingRepositoryImpl()
    override fun storageProvider(): FileStorage = FileStorageImpl()
    override fun playerService(): PlayerService = PlayerServiceImpl()
}