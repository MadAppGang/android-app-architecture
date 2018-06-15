package com.madappgang.architecture.recorder.view_state_model

import java.io.File


data class FolderViewState(val editing: Boolean = false, val file: File? = null, val action: Action? = null, val alertType: AlertType = AlertType.NO_ALERT) {
    enum class Action {
        TOGGLE_EDITING,
        SHOW_RECORD_VIEW,
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

data class PlayerViewState(val uuid: String? = null, val originalFilePath: String = "", val filePath: String = "",
                           val action: Action? = null, val progress: Int? = null, val playerState: PlayerState? = null) {
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