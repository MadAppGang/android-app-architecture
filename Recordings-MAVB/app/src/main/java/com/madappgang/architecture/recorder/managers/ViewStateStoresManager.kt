package com.madappgang.architecture.recorder.managers

import android.arch.lifecycle.MutableLiveData
import com.madappgang.architecture.recorder.data.models.FileModel
import com.madappgang.architecture.recorder.ui.folder_page.FolderViewStateStore
import com.madappgang.architecture.recorder.ui.player_page.PlayerViewStateStore
import com.madappgang.architecture.recorder.ui.recorder_page.RecorderViewStateStore

/**
 * Created by Bohdan Shchavinskiy <bogdan@madappgang.com> on 05.07.2018.
 */
class ViewStateStoresManager {
    var folderViewStateStore: FolderViewStateStore? = null

    var playerViewStateStore: MutableLiveData<PlayerViewStateStore>? = MutableLiveData()
    var recorderViewStateStore: MutableLiveData<RecorderViewStateStore>? = MutableLiveData()

    init {
        folderViewStateStore = FolderViewStateStore()
    }

    fun createPlayerViewStateStore(playFile: FileModel) {
        playerViewStateStore?.value = PlayerViewStateStore(playFile)
    }

    fun createRecorderViewStateStore() {
        recorderViewStateStore?.value = RecorderViewStateStore()
    }

    fun resumeFolderActivity() {
        playerViewStateStore?.value?.clearData()
        recorderViewStateStore?.value?.clearData()
        playerViewStateStore?.value = null
        recorderViewStateStore?.value = null
    }
}