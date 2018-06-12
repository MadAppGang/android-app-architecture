package com.madappgang.architecture.recorder.view_state_model


class ViewStateStore {

    var content = ContentState(folderViews = mutableListOf(FolderViewState(folderUUID = "")), playerView = PlayerViewState(uuid = null))

    fun setPlaySelection(uuid: String?, alreadyApplied: Boolean) {
        content.playerView = PlayerViewState(uuid = uuid)
        //commitAction(alreadyApplied ? SplitViewState.Action.alreadyDismissedDetailView : SplitViewState.Action.changedPlaySelection)
    }

    fun pushFolder(uuid: String) {
        content.folderViews.add(FolderViewState(folderUUID = uuid))
        //commitAction(SplitViewState.Action.pushFolderView)
    }

    fun popToNewDepth(newDepth: Int, alreadyApplied: Boolean) {
        val popDepth = maxOf(1, minOf(content.folderViews.size, newDepth), newDepth)
        content.folderViews.dropLast(content.folderViews.size - popDepth)
        //commitAction(alreadyApplied ? SplitViewState.Action.alreadyPoppedFolderView : SplitViewState.Action.popFolderView)
    }

    fun updateScrollPosition(folderUUID: String, scrollPosition: Double) {
        val index = content.folderViews.indexOfFirst { it.folderUUID == folderUUID }
        if (index != -1) {
            content.folderViews[index].scrollOffset = scrollPosition
            //commitAction(FolderViewState.Action.alreadyUpdatedScrollPosition(folderUUID))
        }
    }

    fun toggleEditing(folderUUID: String) {
        val index = content.folderViews.indexOfFirst { it.folderUUID == folderUUID }
        if (index != -1) {
            content.folderViews[index].editing = !content.folderViews[index].editing
            //commitAction(FolderViewState.Action.toggleEditing(folderUUID))
        }
    }

    fun showCreateFolder(parentUUID: String) {
        content.textAlert = TextAlertState(text = "", parentUUID = parentUUID, recordingUUID = null)
        //commitAction(SplitViewState.Action.showTextAlert)
    }

    fun showRecorder(parentUUID: String) {
        content.recorderView = RecorderViewState(recordState = 0, parentUUID = parentUUID)
        //commitAction(SplitViewState.Action.showRecordView)
    }

    fun dismissRecording() {
        content.recorderView = null
        //commitAction(SplitViewState.Action.dismissRecordView)
    }

    fun showSaveRecording(uuid: String, parentUUID: String) {
        content.textAlert = TextAlertState(text = "", parentUUID = parentUUID, recordingUUID = uuid)
        //commitAction(SplitViewState.Action.showTextAlert)
    }

    fun dismissTextAlert() {
        content.textAlert = null
        //commitAction(SplitViewState.Action.dismissTextAlert)
    }

    fun updateAlertText(text: String) {
        content.textAlert?.text = text
        //commitAction(TextAlertState.Action.updateText)
    }

    fun updatePlayState(playState: Int) {
        content.playerView.playState = playState
        //commitAction(PlayViewState.Action.updatePlayState, sideEffect: true)
    }

    fun updateRecordState(recordState: Int) {
        content.recorderView?.recordState = recordState
        //commitAction(RecordViewState.Action.updateRecordState, sideEffect: true)
    }

    fun togglePlay() {
        if (content.playerView.playState != null) {
            content.playerView.playState = 1   //playState.isPlaying = !playState.isPlaying
            //commitAction(PlayViewState.Action.togglePlay)
        }
    }

    fun changePlaybackPosition(position: Double) {
        if (content.playerView.playState != null) {
            val previousState = content.playerView.playState
            //content.playerView.playState = PlayerState(isPlaying = previousState.isPlaying, progress = position, duration = previousState.duration)
            //commitAction(PlayViewState.Action.changePlaybackPosition)
        }
    }

}