/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 6/7/18.
 */

package com.madappgang.recordings.application

import com.madappgang.recordings.kit.FileManager
import com.madappgang.recordings.kit.Player
import com.madappgang.recordings.kit.Recorder
import com.madappgang.recordings.network.Network
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.CoroutineDispatcher
import kotlinx.coroutines.experimental.android.UI
import java.io.File

internal class DependencyContainer private constructor() {

    lateinit var fileManager: FileManager
    lateinit var recorder: Recorder
    lateinit var player: Player

    internal data class Configurator(val cacheDirectory: File)

    companion object {

        fun newInstance(configurator: Configurator): DependencyContainer {
            val network = Network()
            val fileManager = FileManager(network)
            val recorder = Recorder(configurator.cacheDirectory)
            val player = Player(configurator.cacheDirectory, network)

            return DependencyContainer().apply {
                this.fileManager = fileManager
                this.recorder = recorder
                this.player = player
            }
        }
    }
}