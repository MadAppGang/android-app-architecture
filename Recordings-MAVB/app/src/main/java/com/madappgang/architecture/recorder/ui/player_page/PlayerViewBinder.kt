package com.madappgang.architecture.recorder.ui.player_page

import android.arch.lifecycle.LifecycleOwner
import com.madappgang.architecture.recorder.R
import com.madappgang.architecture.recorder.data.storages.FileStorage

/**
 * Created by Bohdan Shchavinskiy <bogdan@madappgang.com> on 10.09.2018.
 */
class PlayerViewBinder(owner: LifecycleOwner) : PlayerViewBinderCallback {

    override fun setDuration(duration: Int) {
        setRecordDuration(duration)
    }

    override fun seekToPosition(position: Int) {
        setPosition(position)
    }

    override fun changePlayButtonText(textId: Int) {
        setPlayButtonText(textId)
    }

    override fun setProgress(progress: Int) {
        setPosition(progress)
    }

    override fun returnFilePath(filePath: String) {
        originalName = filePath.split("/").last().removeSuffix(FileStorage.recordFormat)
        fileName = originalName
        fileDirectory = filePath.removeSuffix(FileStorage.recordFormat)
        fileDirectory = fileDirectory.substring(0, fileDirectory.length - (fileName.length + 1))
        setLabelText(fileName)
        playerModelAdapter.initPlayer(filePath)
    }

    private var playerModelAdapter = PlayerModelAdapter(owner, this)
    var setLabelText: (text: String) -> Unit = {}
    var setPlayButtonText: (textId: Int) -> Unit = {}
    var setPosition: (progress: Int) -> Unit = {}
    var setRecordDuration: (duration: Int) -> Unit = {}

    private lateinit var fileDirectory: String
    private lateinit var originalName: String
    lateinit var fileName: String

    fun init(path: String) {
        playerModelAdapter.getFilePath(path)
    }

    private fun renameFile() {
        val newFile = "$fileDirectory/$fileName${FileStorage.recordFormat}"
        playerModelAdapter.renameFile(newFile)
    }

    fun playerSeekTo(progress: Int) {

    }

    fun onClickPlay() {
        playerModelAdapter.onClickPlay()
    }

    fun onPause() {
        playerModelAdapter.pauseStatus(true)
        renameFile()
    }

    private fun startPlaying() {
        playerModelAdapter.pauseStatus(false)
        changePlayButtonText(R.string.player_button_pause)
    }
}