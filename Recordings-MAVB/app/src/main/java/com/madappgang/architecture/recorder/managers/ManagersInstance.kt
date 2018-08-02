package com.madappgang.architecture.recorder.managers

import com.madappgang.architecture.recorder.data.repositories.RecordingRepository
import com.madappgang.architecture.recorder.data.services.PlayerService
import com.madappgang.architecture.recorder.data.storages.FileStorage
import com.madappgang.architecture.recorder.ui.folder_page.FolderViewBinder

/**
 * Created by Bohdan Shchavinskiy <bogdan@madappgang.com> on 05.07.2018.
 */
class ManagersInstance(private val managers: Managers) {
    val fileManager: FileManager by lazy { managers.fileManager() }
    val folderViewBinder: FolderViewBinder by lazy { managers.folderViewBinder() }
    val viewStateStore: ViewStateStoresManager by lazy { managers.viewStateStore() }
    val recorder: RecordingRepository by lazy { managers.recordingRepository() }
    val storageProvider: FileStorage by lazy { managers.storageProvider() }
    val playerService: PlayerService by lazy { managers.playerService() }
}