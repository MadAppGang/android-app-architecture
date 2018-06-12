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
        folderView = folderView.copy(action = FolderViewState.Action.TOGGLE_EDITING, editing = isEditing)
        folderViewState.value = folderView
    }

    fun pushFolder(file: File) {
        folderView = folderView.copy(action = FolderViewState.Action.PUSH_FOLDER, file = file)
        folderViewState.value = folderView
    }

    fun popFolder() {
        folderView = folderView.copy(action = FolderViewState.Action.POP_FOLDER)
        folderViewState.value = folderView
    }

    fun setPlaySelection(file: File) {
        folderView = folderView.copy(action = FolderViewState.Action.SHOW_PLAYER_VIEW, file = file)
        folderViewState.value = folderView
    }

    fun showCreateFolder() {
        folderView = folderView.copy(action = FolderViewState.Action.SHOW_CREATE_FOLDER, dialogAction = FolderViewState.Action.SHOW_CREATE_FOLDER)
        folderViewState.value = folderView
    }

    fun showRecorder() {
        folderView = folderView.copy(action = FolderViewState.Action.SHOW_RECORD_VIEW, dialogAction = FolderViewState.Action.SHOW_RECORD_VIEW)
        folderViewState.value = folderView
    }

    fun showSaveRecording() {
        folderView = folderView.copy(action = FolderViewState.Action.SHOW_SAVE_RECORDING)
        folderViewState.value = folderView
    }

    fun dismissAlert() {
        folderView = folderView.copy(action = FolderViewState.Action.DISMISS_ALERT, dialogAction = FolderViewState.Action.DISMISS_ALERT)
        folderViewState.value = folderView
    }

    fun resumeState() {
        folderView = folderView.copy(action = FolderViewState.Action.RESUME_STATE)
        folderViewState.value = folderView
    }

    fun updateRecordDuration(recordDuration: Long) {
        recorderView = recorderView.copy(action = RecorderViewState.Action.UPDATE_RECORD_DURATION, recordDuration = recordDuration)
        recorderViewState.value = recorderView
    }

    fun dismissRecording() {
        recorderView = recorderView.copy(action = RecorderViewState.Action.DISMISS_RECORDING)
        recorderViewState.value = recorderView
    }

    fun updateOriginalFilePath(originalFilePath: String) {
        playerView = playerView.copy(originalFilePath = originalFilePath)
        playerViewState.value = playerView
    }

    fun updateFilePath(filePath: String) {
        playerView = playerView.copy(filePath = filePath)
        playerViewState.value = playerView
    }

    fun updatePlayState(playState: PlayerViewState.PlayerState) {
        playerView = playerView.copy(action = PlayerViewState.Action.UPDATE_PLAY_STATE, playerState = playState)
        playerViewState.value = playerView
    }

    fun playerResumePlay(position: Int) {
        playerView = playerView.copy(action = PlayerViewState.Action.RESUME_PLAYING, progress = position)
        playerViewState.value = playerView
    }

    fun changePlaybackPosition(position: Int) {
        playerView = playerView.copy(action = PlayerViewState.Action.CHANGE_PLAYBACK_POSITION, progress = position)
        playerViewState.value = playerView
    }

    fun getPlayerProgress(): Int = playerView.progress.let { it } ?: 0

    fun playerSeekTo(position: Int) {
        playerView = playerView.copy(action = PlayerViewState.Action.PLAYER_SEEK_TO, progress = position)
        playerViewState.value = playerView
    }

    fun getPlayState(): PlayerViewState.PlayerState? = playerView.playerState
    fun getPlayerFilePath(): String = playerView.filePath
    fun getPlayerStartFilePath(): String = playerView.originalFilePath

}