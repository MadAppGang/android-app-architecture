/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 6/9/18.
 */

package com.madappgang.recordings.network

import com.madappgang.recordings.core.Id
import okhttp3.Request

internal class RequestFactory(private val endpoint: Endpoint) {

    fun <T> makeForFetching(clazz: Class<T>, id: Id): Request {
        TODO("not implemented")
    }

    fun <T> makeForFetchingContent(
            classContent: Class<T>,
            classParent: Class<Any>,
            id: Id
    ): Request {
        TODO("not implemented")
    }

    fun <T> makeForCreate(entity: T): Request {
        TODO("not implemented")
    }

    fun <T> makeForRemove(entity: T): Request {
        TODO("not implemented")
    }
}