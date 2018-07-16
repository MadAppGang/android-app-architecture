/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 7/10/18.
 */

package com.madappgang.recordings.network

class Network constructor(
    endpoint: Endpoint = Endpoint.Staging,
    private val networkSession: NetworkSession = OkHttpSession(endpoint)
) {

    fun <T> executeQuery(request: Request<T>, mapper: ObjectMapper): Result<T> {
        return try {
            val response = networkSession.makeRequest(request, Response.Body::class.java)
            val data = handleResponse(response)
            val entityResult = mapper.toObject(data.value, request.resultType)

            Result.Success(entityResult)
        } catch (e: Throwable) {
            Result.Failure(e)
        }
    }
}

/**
 * @throws NetworkExceptions.UnknownException if response status code  more equals than 400
 */
internal fun <T, R : Response<T>> Network.handleResponse(response: R): R {
    return if (response.statusCode >= 400) {
        when (response.statusCode) {
            400 -> throw NetworkExceptions.BadRequest()
            404 -> throw NetworkExceptions.NotFound()
            else -> throw NetworkExceptions.UnknownException(response.statusCode)
        }
    } else {
        response
    }
}