package com.madappgang.architecture.recorder.ui.player_page

/**
 * Created by Bohdan Shchavinskiy <bogdan@madappgang.com> on 10.09.2018.
 */
interface PlayerModelAdapterCallback {

    fun setDuration(duration: Int)
    fun seekToPosition(progress: Int)

}