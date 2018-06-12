package com.madappgang.architecture.recorder.view_state_model

import android.arch.lifecycle.MutableLiveData
import java.io.File


class ViewStateStore {

    private var folderView = FolderViewState(folderUUID = "")
    private var playerView = PlayerViewState(uuid = null)
    private var recorderView = RecorderViewState()

    val folderViewState: MutableLiveData<FolderViewState> = MutableLiveData()
    val playerViewState: MutableLiveData<PlayerViewState> = MutableLiveData()
    val recorderViewState: MutableLiveData<RecorderViewState> = MutableLiveData()

    init {
        folderViewState.value = folderView
        playerViewState.value = playerView
        recorderViewState.value = recorderView
    }

    fun toggleEditing(isEditing: Boolean) {
        folderViewState.value = folderView.copy(action = FolderViewState.Action.TOGGLE_EDITING, editing = isEditing)
    }

    fun pushFolder(file: File) {
        folderViewState.value = folderView.copy(action = FolderViewState.Action.PUSH_FOLDER, file = file)
    }

    fun popFolder() {
        folderViewState.value = folderView.copy(action = FolderViewState.Action.POP_FOLDER)
    }

    fun setPlaySelection(file: File) {
        folderViewState.value = folderView.copy(action = FolderViewState.Action.SHOW_PLAYER_VIEW, file = file)
    }

    fun showCreateFolder() {
        folderViewState.value = folderView.copy(action = FolderViewState.Action.SHOW_CREATE_FOLDER)
    }

    fun showRecorder() {
        folderViewState.value = folderView.copy(action = FolderViewState.Action.SHOW_RECORD_VIEW)
    }

    fun showSaveRecording() {
        folderViewState.value = folderView.copy(action = FolderViewState.Action.SHOW_SAVE_RECORDING)
    }

    fun dismissAlert() {
        folderViewState.value = folderView.copy(action = FolderViewState.Action.DISMISS_ALERT)
    }

    fun updateRecordDuration(recordDuration: Long) {
        recorderViewState.value = recorderView.copy(action = RecorderViewState.Action.UPDATE_RECORD_DURATION, recordDuration = recordDuration)
    }

    fun dismissRecording() {
        recorderViewState.value = recorderView.copy(action = RecorderViewState.Action.DISMISS_RECORDING)
    }

    fun updatePlayState(playState: Int) {
        //content.playerView.playState = playState
        //commitAction(PlayViewState.Action.updatePlayState, sideEffect: true)
    }

    fun togglePlay() {
//        if (content.playerView.playState != null) {
//            content.playerView.playState = 1   //playState.isPlaying = !playState.isPlaying
//            //commitAction(PlayViewState.Action.togglePlay)
//        }
    }

    fun changePlaybackPosition(position: Double) {
//        if (content.playerView.playState != null) {
//            val previousState = content.playerView.playState
//            //content.playerView.playState = PlayerState(isPlaying = previousState.isPlaying, progress = position, duration = previousState.duration)
//            //commitAction(PlayViewState.Action.changePlaybackPosition)
//        }
    }

}