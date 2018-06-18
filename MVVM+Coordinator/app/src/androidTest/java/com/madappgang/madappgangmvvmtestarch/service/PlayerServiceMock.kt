package com.madappgang.madappgangmvvmtestarch.service

import com.madappgang.madappgangmvvmtestarch.model.service.PlayerService

/**
 * Created by Serhii Chaban sc@madappgang.com on 15.RER06.18.
 */
class PlayerServiceMock : PlayerService {
    override var onComplete: () -> Unit = {}
    override var onError: (errorCode: Int, message: String) -> Boolean = { e, m -> false }
    override var onInit: () -> Unit = {}
    override val duration: Int = 1000000
    private var position = 0
    override val currentPosition: Int
        get() {
            val pos = position + 3000
            position = pos
            return pos
        }
    override val isPlaying: Boolean get() = playing
    var playing = false
    override fun setDataSource(source: String) {

    }

    override fun seekTo(seek: Int) {
        position = seek
    }

    override fun startPlay() {
        playing = true
    }

    override fun start() {
        playing = !playing
    }

    override fun stop() {
        playing = false
    }

    override fun reset() {
    }

    override fun release() {
    }

    override fun pause() {
        playing = false
    }

}