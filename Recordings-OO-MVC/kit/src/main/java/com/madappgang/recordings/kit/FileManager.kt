/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 6/8/18.
 */

package com.madappgang.recordings.kit

import com.madappgang.recordings.core.Foldable
import com.madappgang.recordings.core.Folder
import com.madappgang.recordings.core.Result
import com.madappgang.recordings.network.Constraint
import com.madappgang.recordings.network.FetchingOptions
import com.madappgang.recordings.network.Network
import com.madappgang.recordings.network.add

class FileManager(private val network: Network) {

    fun <T : Foldable> fetchEntity(entityType: Class<T>, fetchingOptions: FetchingOptions): Result<T> {
        return network.fetchEntity(entityType, fetchingOptions)
    }

    fun fetchList(rootFolder: Folder): Result<List<Foldable>> {

        val fetchingOptions = FetchingOptions()
            .add(Constraint.FoldablePath(rootFolder.getFullPath()))

        return network.fetchList(Foldable::class.java, fetchingOptions)
    }

    fun <T : Foldable> add(foldable: T): Result<T> {
        return network.createEntity(foldable)
    }

    fun remove(foldable: Foldable): Result<Unit> {
        return network.removeEntity(foldable)
    }

    fun validateName(name: String) = name.isEmpty()

}

sealed class FileExceptions(message: String) : Throwable(message) {

    object FileNameIsEmptyException : FileExceptions("")

}