package com.madappgang.architecture.recorder.view_state_model

import android.arch.lifecycle.MutableLiveData
import java.io.File


class ViewStateStore {

    val folderViewState: MutableLiveData<FolderViewState> = MutableLiveData()
    val playerViewState: MutableLiveData<PlayerViewState> = MutableLiveData()
    val recorderViewState: MutableLiveData<RecorderViewState> = MutableLiveData()

    init {
        folderViewState.value = FolderViewState()
        playerViewState.value = PlayerViewState()
        recorderViewState.value = RecorderViewState()
    }

    fun toggleEditing(isEditing: Boolean) {
        folderViewState.value = folderViewState.value!!.copy(action = FolderViewState.Action.TOGGLE_EDITING, editing = isEditing)
    }

    fun pushFolder(file: File) {
        folderViewState.value = folderViewState.value!!.copy(action = FolderViewState.Action.PUSH_FOLDER, file = file)
    }

    fun popFolder(file: File) {
        folderViewState.value = folderViewState.value!!.copy(action = FolderViewState.Action.POP_FOLDER, file = file)
    }

    fun showCreateFolder() {
        folderViewState.value = folderViewState.value!!.copy(action = FolderViewState.Action.SHOW_ALERT, alertType = FolderViewState.AlertType.CREATE_FOLDER)
    }

    fun showSaveRecording() {
        folderViewState.value = folderViewState.value!!.copy(action = FolderViewState.Action.SHOW_ALERT, alertType = FolderViewState.AlertType.SAVE_RECORDING)
    }

    fun dismissAlert() {
        folderViewState.value = folderViewState.value!!.copy(action = FolderViewState.Action.DISMISS_ALERT, alertType = FolderViewState.AlertType.NO_ALERT)
    }

    fun updateRecordDuration(recordDuration: Long) {
        recorderViewState.value = recorderViewState.value!!.copy(action = RecorderViewState.Action.UPDATE_RECORD_DURATION, recordDuration = recordDuration)
    }

    fun updateOriginalFilePath(originalFilePath: String) {
        playerViewState.value = playerViewState.value!!.copy(originalFilePath = originalFilePath)
    }

    fun updateFilePath(filePath: String) {
        playerViewState.value = playerViewState.value!!.copy(filePath = filePath)
    }

    fun updatePlayState(playState: PlayerViewState.PlayerState) {
        playerViewState.value = playerViewState.value!!.copy(action = PlayerViewState.Action.UPDATE_PLAY_STATE, playerState = playState)
    }

    fun changePlaybackPosition(position: Int) {
        playerViewState.value = playerViewState.value!!.copy(action = PlayerViewState.Action.CHANGE_PLAYBACK_POSITION, progress = position)
    }

    fun playerSeekTo(position: Int) {
        playerViewState.value = playerViewState.value!!.copy(action = PlayerViewState.Action.PLAYER_SEEK_TO, progress = position)
    }
}