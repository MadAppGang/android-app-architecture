/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 7/10/18.
 */

package com.madappgang.recordings.applications

import com.madappgang.recordings.models.Foldable
import com.madappgang.recordings.models.Player
import com.madappgang.recordings.models.Recorder
import com.madappgang.recordings.network.Network
import java.io.File

internal class DependencyContainer private constructor() {

    lateinit var recorder: Recorder
    lateinit var player: Player


    internal data class Configurator(val cacheDirectory: File)

    companion object {

        fun newInstance(configurator: Configurator): DependencyContainer {
            val recorder = Recorder(configurator.cacheDirectory)
            val player = Player()
            Foldable.network =  Network()

            return DependencyContainer().apply {
                this.recorder = recorder
                this.player = player
            }
        }
    }
}