/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 6/8/18.
 */

package com.madappgang.recordings.network

import com.madappgang.recordings.core.Id

interface Network {

    fun <T> fetchEntity(clazz: Class<T>, id: Id): Result<T> {
        TODO("not implemented")
    }

    fun <T> fetchContent(clazz: Class<T>, id: Id): Result<List<T>> {
        TODO("not implemented")
    }

    fun <T> createEntity(entity: T): Result<T> {
        TODO("not implemented")
    }

    fun <T> removeEntity(entity: T): Result<Unit> {
        TODO("not implemented")
    }

}