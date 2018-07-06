package com.madappgang.architecture.recorder.managers

import com.madappgang.architecture.recorder.ui.folder_page.FolderViewStateStore
import com.madappgang.architecture.recorder.ui.player_page.PlayerViewStateStore
import com.madappgang.architecture.recorder.ui.recorder_page.RecorderViewStateStore

/**
 * Created by Bohdan Shchavinskiy <bogdan@madappgang.com> on 05.07.2018.
 */
class ViewStateStoresManager {
    var folderViewStateStore: FolderViewStateStore? = null
    var playerViewStateStore: PlayerViewStateStore? = null
    var recorderViewStateStore: RecorderViewStateStore? = null

    init {
        folderViewStateStore = FolderViewStateStore()
    }

    fun createPlayerViewStateStore(){
        playerViewStateStore = PlayerViewStateStore()
    }

    fun createRecorderViewStateStore(){
        recorderViewStateStore = RecorderViewStateStore()
    }

    fun resumeFolderActivity() {
        playerViewStateStore?.clearData()
        recorderViewStateStore?.clearData()
        playerViewStateStore = null
        recorderViewStateStore = null
    }
}