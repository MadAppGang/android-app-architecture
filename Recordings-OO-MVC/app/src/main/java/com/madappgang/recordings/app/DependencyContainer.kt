package com.madappgang.recordings.app

import com.madappgang.recordings.reckit.FileManager
import com.madappgang.recordings.reckit.Player
import com.madappgang.recordings.reckit.Recorder
import com.madappgang.recordings.recnetwork.Network

/**
 * Created by Andrii Fedorov afedorov@madappgang.com on 6/7/18.
 */
class DependencyContainer {
    val fileManager by lazy { FileManager(network) }
    val recorder by lazy { Recorder() }
    val player by lazy { Player(network) }

//    TODO assign value
    private lateinit var network: Network
}