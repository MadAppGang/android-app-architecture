/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 6/13/18.
 */

package com.madappgang.recordings.network

import okio.BufferedSource

sealed class Response<T>(val statusCode: Int, val value: T) {

    class Body(statusCode: Int, value: String) : Response<String>(statusCode, value)

    class BufferedBody(
        statusCode: Int,
        value: BufferedSource
    ) : Response<BufferedSource>(statusCode, value)

}