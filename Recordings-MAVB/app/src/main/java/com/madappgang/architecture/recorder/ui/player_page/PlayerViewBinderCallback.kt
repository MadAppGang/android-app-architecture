package com.madappgang.architecture.recorder.ui.player_page

/**
 * Created by Bohdan Shchavinskiy <bogdan@madappgang.com> on 10.09.2018.
 */
interface PlayerViewBinderCallback {

    fun setProgress(progress: Int)
    fun returnFilePath(filePath: String)
    fun changePlayButtonText(textId: Int)
    fun seekToPosition(position: Int)
    fun setDuration(duration: Int)

}