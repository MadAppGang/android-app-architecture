package com.madappgang.recordings.reckit

import com.madappgang.recordings.reccore.Track
import com.madappgang.recordings.recnetwork.Network
import java.io.File

/**
 * Created by Andrii Fedorov afedorov@madappgang.com on 6/7/18.
 */

class Player(val network: Network) {
    var onPlay: ((File) -> Unit)? = null
    var onStop: (() -> Unit)? = null

    fun play(track: Track) {
        TODO("not implemented")
    }

    fun stop() {
        TODO("not implemented")
    }
}