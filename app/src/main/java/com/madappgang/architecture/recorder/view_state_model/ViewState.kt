package com.madappgang.architecture.recorder.view_state_model


data class ContentState(var folderViews: MutableList<FolderViewState>, var playerView: PlayerViewState, var recorderView: RecorderViewState? = null, var textAlert: TextAlertState? = null)

data class FolderViewState(val folderUUID: String, var editing: Boolean = false, var scrollOffset: Double = 0.0) {
    enum class Item {
        TOGGLE_EDITING,
        ALREADY_UPDATED_SCROLL_POSITION
    }
}

data class RecorderViewState(var recordState: Int, val parentUUID: String) {
    enum class Item {
        UPDATE_RECORD_STATE
    }
}

data class PlayerViewState(val uuid: String?, var playState: Int? = null) {
    enum class Item {
        UPDATE_PLAY_STATE,
        TOGGLE_PLAY,
        CHANGE_PLAYBACK_POSITION
    }
}

data class TextAlertState(var text: String, val parentUUID: String, val recordingUUID: String?) {
    enum class Item {
        UPDATE_TEXT
    }
}