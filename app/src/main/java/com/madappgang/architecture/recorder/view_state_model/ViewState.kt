package com.madappgang.architecture.recorder.view_state_model

import java.io.File


data class FolderViewState(var editing: Boolean = false, var file: File? = null, var action: Action? = null, var alertType: AlertType = AlertType.NO_ALERT) {
    enum class Action {
        TOGGLE_EDITING,
        SHOW_RECORD_VIEW,
        SHOW_PLAYER_VIEW,
        SHOW_SAVE_RECORDING,
        PUSH_FOLDER,
        POP_FOLDER,
        SHOW_ALERT,
        DISMISS_ALERT
    }

    enum class AlertType {
        NO_ALERT,
        CREATE_FOLDER,
        SAVE_RECORDING
    }
}

data class RecorderViewState(val recordDuration: Long = 0, val action: RecorderViewState.Action? = null) {
    enum class Action {
        UPDATE_RECORD_DURATION,
        DISMISS_RECORDING
    }
}

data class PlayerViewState(val uuid: String?, var originalFilePath: String = "", var filePath: String = "",
                           val action: Action? = null, var progress: Int? = null, var playerState: PlayerState? = null) {
    enum class Action {
        UPDATE_PLAY_STATE,
        RESUME_PLAYING,
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