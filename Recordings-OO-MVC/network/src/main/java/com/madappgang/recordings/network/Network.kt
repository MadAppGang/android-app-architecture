/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 6/8/18.
 */

package com.madappgang.recordings.network

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.madappgang.recordings.core.Id
import com.madappgang.recordings.core.NetworkExceptions
import com.madappgang.recordings.core.Result
import com.madappgang.recordings.network.mapper.FoldableMapper
import com.madappgang.recordings.network.mapper.FolderMapper
import com.madappgang.recordings.network.mapper.NetworkMapper
import com.madappgang.recordings.network.mapper.TrackMapper
import java.io.File
import okio.Okio

class Network internal constructor(
    private val endpoint: Endpoint = Endpoint.Staging,
    private val networkSession: NetworkSession = OkHttpSession(),
    private val requestFactory: RequestFactory = RequestFactory(endpoint, createGson()),
    private val networkMapper: NetworkMapper = NetworkMapper(createGson())
) {

    constructor(
        endpoint: Endpoint = Endpoint.Staging,
        networkSession: NetworkSession = OkHttpSession()
    ) : this(
        endpoint,
        networkSession,
        RequestFactory(endpoint, createGson()),
        NetworkMapper(createGson())
    )

    fun <T> fetchEntity(requestType: Class<T>, id: Id): Result<T> {
        val request = try {
            requestFactory.makeForFetching(requestType, id)
        } catch (e: Throwable) {
            return Result.Failure(e)
        }

        return try {
            val response = networkSession.makeRequest(request, Response.Body::class.java)
            val data = handleResponse(response)
            val entityResult = networkMapper.mapToEntity(data.value, requestType)

            Result.Success(entityResult)
        } catch (e: Throwable) {
            Result.Failure(e)
        }
    }

    fun <T> fetchList(requestType: Class<T>, fetchingOptions: FetchingOptions): Result<List<T>> {
        val request = try {
            requestFactory.makeForFetching(requestType, fetchingOptions)
        } catch (e: Throwable) {
            return Result.Failure(e)
        }

        return try {
            val response = networkSession.makeRequest(request, Response.Body::class.java)
            val data = handleResponse(response)
            val entity: List<T> = networkMapper.mapToList(data.value)

            Result.Success(entity)
        } catch (e: Throwable) {
            Result.Failure(e)
        }
    }

    fun <T> createEntity(entity: T, requestType: Class<T>): Result<T> {
        val request = try {
            requestFactory.makeForCreate(entity)
        } catch (e: Throwable) {
            return Result.Failure(e)
        }

        return try {
            val response = networkSession.makeRequest(request, Response.Body::class.java)
            val data = handleResponse(response)
            val entityResult = networkMapper.mapToEntity(data.value, requestType)

            Result.Success(entityResult)
        } catch (e: Throwable) {
            Result.Failure(e)
        }
    }

    fun <T> removeEntity(entity: T, requestType: Class<T>): Result<Unit> {
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

    fun downloadFile(url: String, destination: File): Result<Unit> {
        val request = try {
            requestFactory.makeForDownload(url)
        } catch (e: Throwable) {
            return Result.Failure(e)
        }

        return try {
            val response = networkSession.makeRequest(request, Response.BufferedBody::class.java)
            val data = handleResponse(response)

            val sink = Okio.buffer(Okio.sink(destination))
            sink.writeAll(data.value)
            sink.close()

            Result.Success(Unit)
        } catch (e: Throwable) {
            Result.Failure(e)
        }
    }
}

internal fun <T, R : Response<T>> Network.handleResponse(response: R): R {
    return if (response.statusCode > 400) {
        throw NetworkExceptions.UnknownException()
    } else {
        response
    }
}

internal fun createGson() = GsonBuilder()
    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
    .registerTypeAdapter(FoldableMapper::class.java, FoldableMapper())
    .registerTypeAdapter(FolderMapper::class.java, FolderMapper())
    .registerTypeAdapter(TrackMapper::class.java, TrackMapper())
    .create()