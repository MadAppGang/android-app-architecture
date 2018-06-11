/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 6/7/18.
 */

package com.madappgang.recordings

import com.madappgang.recordings.kit.FileManager
import com.madappgang.recordings.kit.Player
import com.madappgang.recordings.kit.Recorder
import com.madappgang.recordings.network.Network

class DependencyContainer {

    val fileManager by lazy { FileManager(network) }
    val recorder by lazy { Recorder() }
    val player by lazy { Player(network) }

    // TODO assign value
    private lateinit var network: Network

}