/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 6/8/18.
 */

package com.madappgang.recordings.network

import com.google.gson.internal.LinkedTreeMap
import com.madappgang.recordings.core.Result
import com.madappgang.recordings.network.mapper.NetworkMapper

class Network constructor(
    private val endpoint: Endpoint = Endpoint.Staging,
    private val networkSession: NetworkSession = OkHttpSession()
) {

    private val networkMapper: NetworkMapper = NetworkMapper()
    private val requestFactory: RequestFactory = RequestFactory(endpoint, networkMapper)


    fun <T: Any> fetchEntity(entityType: Class<T>, fetchingOptions: FetchingOptions): Result<T> {
        val request = try {
            requestFactory.makeForFetching(entityType, fetchingOptions)
        } catch (e: Throwable) {
            return Result.Failure(e)
        }
        return try {
            val response = networkSession.makeRequest(request, Response.Body::class.java)
            val data = handleResponse(response)
            val entityResult = networkMapper.mapToEntity(data.value, entityType)

            Result.Success(entityResult)
        } catch (e: Throwable) {
            Result.Failure(e)
        }
    }

    fun <T> fetchList(entityType: Class<T>, fetchingOptions: FetchingOptions): Result<List<T>> {
        val request = try {
            requestFactory.makeForFetching(entityType, fetchingOptions)
        } catch (e: Throwable) {
            return Result.Failure(e)
        }

        return try {
            val response = networkSession.makeRequest(request, Response.Body::class.java)
            val data = handleResponse(response)
            val entity: List<T> = networkMapper.mapToList(data.value, entityType)

            Result.Success(entity)
        } catch (e: Throwable) {
            Result.Failure(e)
        }
    }

    fun <T: Any> createEntity(entity: T): Result<T> {
        val request = try {
            requestFactory.makeForCreate(entity)
        } catch (e: Throwable) {
            return Result.Failure(e)
        }

        return try {
            val response = networkSession.makeRequest(request, Response.Body::class.java)
            val data = handleResponse(response)
            val entityResult = networkMapper.mapToEntity(data.value, entity::class.java)

            Result.Success(entityResult)
        } catch (e: Throwable) {
            Result.Failure(e)
        }
    }

    fun <T> removeEntity(entity: T): Result<Unit> {
        val request = try {
            requestFactory.makeForRemove(entity)
        } catch (e: Throwable) {
            return Result.Failure(e)
        }

        return try {
            val response = networkSession.makeRequest(request, Response.Body::class.java)
            handleResponse(response)

            Result.Success(Unit)
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
        throw NetworkExceptions.UnknownException()
    } else {
        response
    }
}