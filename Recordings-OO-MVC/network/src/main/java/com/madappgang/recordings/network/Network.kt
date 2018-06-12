/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 6/8/18.
 */

package com.madappgang.recordings.network

import com.madappgang.recordings.core.Id
import com.madappgang.recordings.core.Result
import java.io.File

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

    fun <T> fetchList(clazz: Class<T>, fetchingOptions: FetchingOptions): Result<List<T>> {
        TODO("not implemented")
    }

    fun <T> createEntity(entity: T): Result<T> {
        TODO("not implemented")
    }

    fun <T> removeEntity(entity: T): Result<Unit> {
        TODO("not implemented")
    }

    fun downloadFile(url: String, destinition: File): Result<Unit> {
        TODO("not implemented")
    }
}