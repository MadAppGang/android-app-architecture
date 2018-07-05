package com.madappgang.architecture.recorder.ui.player_page

import android.arch.lifecycle.MutableLiveData

/**
 * Created by Bohdan Shchavinskiy <bogdan@madappgang.com> on 05.07.2018.
 */
class PlayerViewStateStore {

    var playerView: PlayerViewState? = PlayerViewState()
    var playerViewState: MutableLiveData<PlayerViewState>? = MutableLiveData()

    init {
        playerViewState?.value = playerView
    }

    fun updateOriginalFilePath(originalFilePath: String) {
        playerView = playerView?.copy(originalFilePath = originalFilePath)
        playerViewState?.value = playerView
    }

    fun updateFilePath(filePath: String) {
        playerView = playerView?.copy(filePath = filePath)
        playerViewState?.value = playerView
    }

    fun updatePlayState(playState: PlayerViewState.PlayerState) {
        playerView = playerView?.copy(action = PlayerViewState.Action.UPDATE_PLAY_STATE, playerState = playState)
        playerViewState?.value = playerView
    }

    fun changePlaybackPosition(position: Int) {
        playerView = playerView?.copy(action = PlayerViewState.Action.CHANGE_PLAYBACK_POSITION, progress = position)
        playerViewState?.value = playerView
    }

    fun playerSeekTo(position: Int) {
        playerView = playerView?.copy(action = PlayerViewState.Action.PLAYER_SEEK_TO, progress = position)
        playerViewState?.value = playerView
    }

    fun clearData() {
        playerView = null
        playerViewState = null
    }
}