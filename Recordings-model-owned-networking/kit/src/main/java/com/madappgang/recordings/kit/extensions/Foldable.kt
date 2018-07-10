/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 7/10/18.
 */

package com.madappgang.recordings.kit.extensions

import com.madappgang.recordings.core.Foldable
import com.madappgang.recordings.core.Result
import com.madappgang.recordings.network.Network

lateinit var network: Network

private val Foldable.network: Network
    get() = com.madappgang.recordings.kit.extensions.network

fun Foldable.save() : Result<Void> {
    TODO("not implemented")
}

fun Foldable.remove() : Result<Void> {
    TODO("not implemented")
}

fun Foldable.fetch() : Result<Void> {
    TODO("not implemented")
}
