package com.madappgang.architecture.recorder.ui.folder_page

import com.madappgang.architecture.recorder.data.models.FileModel

/**
 * Created by Bohdan Shchavinskiy <bogdan@madappgang.com> on 05.07.2018.
 */
data class FolderViewState(var editing: Boolean = false, var file: FileModel? = null, var action: Action? = null, var alertType: AlertType = AlertType.NO_ALERT) {
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