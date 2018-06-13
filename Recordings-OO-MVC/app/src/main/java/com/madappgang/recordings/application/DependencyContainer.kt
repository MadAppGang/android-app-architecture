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

class DependencyContainer(private val tempDirectory: File) {

    val uiContext: CoroutineDispatcher by lazy { UI }
    val bgContext: CoroutineDispatcher by lazy { CommonPool }

    val fileManager by lazy { FileManager(network) }
    val recorder by lazy { Recorder() }
    val player by lazy { Player(tempDirectory, network, bgContext) }


    private val network: Network = Network()

}