package com.madappgang.recordings.kit

import com.madappgang.recordings.core.Track
import com.madappgang.recordings.network.Network
import java.io.File


/**
 * Created by Andrii Fedorov afedorov@madappgang.com on 6/8/18.
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