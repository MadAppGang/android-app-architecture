/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 6/8/18.
 */

package com.madappgang.recordings.kit

import com.madappgang.recordings.core.Foldable
import com.madappgang.recordings.core.Folder
import com.madappgang.recordings.core.Id
import com.madappgang.recordings.core.Result
import com.madappgang.recordings.network.*

class FileManager(private val network: Network) {

    fun <T : Foldable> fetchEntity(entityType: Class<T>, id: Id): Result<T> {
        return network.fetchEntity(entityType, id)
    }

    fun fetchList(rootFolder: Folder): Result<List<Foldable>> {
        val ownerId = rootFolder.folderId ?: Id("")

        val fetchingOptions = FetchingOptions()
            .add(Constraint.Owner(Folder::class.java))
            .add(Constraint.OwnerId(ownerId))

        return network.fetchList(Foldable::class.java, fetchingOptions)
    }

    fun <T : Foldable> add(foldable: T, entityType: Class<T>): Result<T> {
        return network.createEntity(foldable, entityType)
    }

    fun remove(foldable: Foldable): Result<Unit> {
        return network.removeEntity(foldable, Foldable::class.java)
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