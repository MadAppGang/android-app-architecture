package com.madappgang.architecture.recorder.ui.player_page

/**
 * Created by Bohdan Shchavinskiy <bogdan@madappgang.com> on 05.07.2018.
 */
data class PlayerViewState(val uuid: String? = null, var originalFilePath: String = "", var filePath: String = "",
                           val action: Action? = null, var progress: Int? = null, var playerState: PlayerState? = null) {
    enum class Action {
        UPDATE_PLAY_STATE,
        CHANGE_PLAYBACK_POSITION,
        UPDATE_FILE_NAME,
        PLAYER_SEEK_TO
    }

    enum class PlayerState {
        PLAY,
        PAUSE,
        STOP
    }
}