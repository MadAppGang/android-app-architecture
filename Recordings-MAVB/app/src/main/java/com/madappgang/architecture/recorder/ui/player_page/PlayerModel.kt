package com.madappgang.architecture.recorder.ui.player_page

import android.arch.lifecycle.MutableLiveData
import android.os.Handler
import com.madappgang.architecture.recorder.application.AppInstance
import com.madappgang.architecture.recorder.data.services.PlayerService

/**
 * Created by Bohdan Shchavinskiy <bogdan@madappgang.com> on 10.09.2018.
 */
class PlayerModel(private val playerModelAdapterCallback: PlayerModelAdapterCallback) : PlayerService.PlayerCallback{

    private var playerService: PlayerService = AppInstance.managersInstance.playerService
    private val fileManager = AppInstance.managersInstance.fileManager
    private val viewStateStore = AppInstance.managersInstance.viewStateStore
    private val playerViewStateStore = viewStateStore.playerViewStateStore?.value
    private val handler = Handler()
    private var isPlayerPause = false

    fun currentViewState() = playerViewStateStore?.playerView ?: PlayerViewState()

    fun onClickPlay() {
        if (playerService.isPlaying()) {
            playerViewStateStore?.updatePlayState(PlayerViewState.PlayerState.PAUSE)
        } else {
            playerViewStateStore?.updatePlayState(PlayerViewState.PlayerState.PLAY)
        }
    }

    fun seekToPosition(progress: Int) {
        playerService.seekTo(progress)
    }

    fun updatePlayerStatus(isPlayerPause: Boolean) {
        this.isPlayerPause = isPlayerPause
        if (isPlayerPause) {
            playerService.pause()
        } else {
            playerService.play()
            startPlayProgressUpdate()
        }
    }

    fun startPlayProgressUpdate() {
        if (playerService.isPlaying()) {
            playerViewStateStore?.changePlaybackPosition(playerService.getCurrentPosition())
            val notification = Runnable { startPlayProgressUpdate() }
            handler.postDelayed(notification, 10)
        } else if (!isPlayerPause) {
            playerViewStateStore?.updatePlayState(PlayerViewState.PlayerState.STOP)
        }
    }

    fun getViewState(): MutableLiveData<PlayerViewState>? {
        return playerViewStateStore?.playerViewState
    }

    fun getFilePath(path: String): String {
        val currentState = currentViewState()
        val resumePlay = currentState.originalFilePath.isNotEmpty()
                && currentState.filePath.isNotEmpty()
                && path == currentState.originalFilePath

        val filePath = if (resumePlay)
            currentState.filePath
        else ""

        if (resumePlay && currentState.playerState != null && currentState.playerState != PlayerViewState.PlayerState.STOP) {
            val progress = currentState.progress.let { it } ?: 0
            seekToPosition(progress)
            playerModelAdapterCallback.seekToPosition(progress)
            if (currentState.playerState == PlayerViewState.PlayerState.PLAY) {
                updatePlayerStatus(false)
            } else {
                playerService.play()
                playerService.pause()
                updatePlayerStatus(true)
            }
        }
        if (filePath.isNullOrEmpty()) updateFilePath(path)
        return filePath
    }

    override fun setDuration(duration: Int) {
        playerModelAdapterCallback.setDuration(duration)
    }

    fun updateFilePath(path: String) {
        playerViewStateStore?.updateFilePath(path)
        playerViewStateStore?.updateOriginalFilePath(path)
        resetPlaybackPosition()
    }

    fun renameFile(newFile: String) {
        val currentState = currentViewState()
        if (currentState.filePath != newFile) {
            if (fileManager.renameFile(currentState.filePath, newFile)) {
                playerViewStateStore?.updateFilePath(newFile)
            }
        }
    }

    fun resetPlaybackPosition() {
        playerViewStateStore?.changePlaybackPosition(0)
    }

    fun pause() {
        playerService.pause()
    }

    fun initPlayer(filePath: String) {
        playerService.init(filePath, this)
    }
}