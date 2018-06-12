/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 6/8/18.
 */

package com.madappgang.recordings.kit

import com.madappgang.recordings.core.Foldable
import com.madappgang.recordings.core.Folder
import com.madappgang.recordings.core.Id
import com.madappgang.recordings.network.Network
import com.madappgang.recordings.network.Result

class FileManager(private val network: Network) {

    fun <T : Foldable> fetch(id: Id): Result<T> {
        TODO("not implemented")
    }

    fun fetchContent(folder: Folder): Result<List<Foldable>> {
        TODO("not implemented")
    }

    fun <T : Foldable> add(foldable: T): Result<T> {
        TODO("not implemented")
    }

    fun remove(foldable: Foldable): Result<Unit> {
        TODO("not implemented")
    }
}

sealed class FileExceptions(message: String) : Throwable(message) {

    object FileNameIsEmptyException : FileExceptions("")

}

fun FileManager.validName(name: String) {
    if (name.isEmpty()) {
        throw FileExceptions.FileNameIsEmptyException
    }
}