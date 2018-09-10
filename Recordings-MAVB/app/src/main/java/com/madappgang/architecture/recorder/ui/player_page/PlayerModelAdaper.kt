package com.madappgang.architecture.recorder.ui.player_page

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import com.madappgang.architecture.recorder.R

/**
 * Created by Bohdan Shchavinskiy <bogdan@madappgang.com> on 10.09.2018.
 */
class PlayerModelAdapter(owner: LifecycleOwner, private var playerViewBinderCallback: PlayerViewBinderCallback) : PlayerModelAdapterCallback {

    override fun setDuration(duration: Int) {
        playerViewBinderCallback.setDuration(duration)
    }

    private val playerModel = PlayerModel(this)

    init {
        playerModel.getViewState()?.observe(owner, Observer {
            it?.let { handle(it) }
        })
    }

    private fun handle(viewState: PlayerViewState) {
        when (viewState.action) {
            PlayerViewState.Action.CHANGE_PLAYBACK_POSITION -> {
                val progress = viewState.progress.let { it } ?: 0
                playerViewBinderCallback.setProgress(progress)
            }
            PlayerViewState.Action.PLAYER_SEEK_TO -> {
                playerViewBinderCallback.seekToPosition(viewState.progress.let { it } ?: 0)
            }
            PlayerViewState.Action.UPDATE_PLAY_STATE -> {
                when (viewState.playerState) {
                    PlayerViewState.PlayerState.PLAY -> {
                        playerModel.updatePlayerStatus(false)
                        playerViewBinderCallback.changePlayButtonText(R.string.player_button_pause)
                    }
                    PlayerViewState.PlayerState.PAUSE -> {
                        playerModel.updatePlayerStatus(true)
                        playerViewBinderCallback.changePlayButtonText(R.string.player_button_resume_play)
                    }
                    PlayerViewState.PlayerState.STOP -> {
                        playerModel.updatePlayerStatus(false)
                        playerModel.pause()
                        playerModel.resetPlaybackPosition()
                        playerViewBinderCallback.changePlayButtonText(R.string.player_button_play)
                    }
                }
            }
        }
    }

    fun onClickPlay() {
        playerModel.onClickPlay()
    }

    override fun seekToPosition(progress: Int) {
        playerViewBinderCallback.seekToPosition(progress)
    }

    fun renameFile(newFile: String) {
        playerModel.renameFile(newFile)
    }

    fun pauseStatus(isPlayerPause: Boolean) {
        playerModel.updatePlayerStatus(isPlayerPause)
    }

    fun getFilePath(path: String) {
        val filePath = playerModel.getFilePath(path)
        if (filePath.isNotEmpty()) {
            playerViewBinderCallback.returnFilePath(filePath)
        } else {
            playerViewBinderCallback.changePlayButtonText(R.string.player_button_play)
            playerViewBinderCallback.returnFilePath(path)
        }
    }

    fun initPlayer(filePath:String){
        playerModel.initPlayer(filePath)
    }
}