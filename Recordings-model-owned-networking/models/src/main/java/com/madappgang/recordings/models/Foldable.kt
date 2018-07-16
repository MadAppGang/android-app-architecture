/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 7/9/18.
 */

package com.madappgang.recordings.models

import com.madappgang.recordings.models.mapper.GsonMapper
import com.madappgang.recordings.network.*


abstract class Foldable {
    abstract var path: String
    abstract var name: String

    companion object {
        var mapper = GsonMapper()
        lateinit var network: Network
    }

    val fullPath: String
        get() = if (path.isEmpty()) "" else "$path/$name"

    abstract fun remove(): Result<Void>
}

fun Foldable.Companion.validateName(name: String) = name.isNotBlank()