/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 6/8/18.
 */

package com.madappgang.recordings.network

import com.madappgang.recordings.core.Id

class Network internal constructor(
        private val endpoint: Endpoint = Endpoint.Staging,
        private val networkSession: NetworkSession = OkHttpSession(),
        private val requestFactory: RequestFactory = RequestFactory(endpoint)
) {

    constructor(
            endpoint: Endpoint = Endpoint.Staging,
            networkSession: NetworkSession = OkHttpSession()
    ) : this(endpoint, networkSession, RequestFactory(endpoint))

    fun <T> fetchEntity(clazz: Class<T>, id: Id): Result<T> {
        TODO("not implemented")
    }

    fun <T> fetchContent(classContent: Class<T>, classParent: Class<Any>, id: Id): Result<List<T>> {
        TODO("not implemented")
    }

    fun <T> createEntity(entity: T): Result<T> {
        TODO("not implemented")
    }

    fun <T> removeEntity(entity: T): Result<Unit> {
        TODO("not implemented")
    }
}