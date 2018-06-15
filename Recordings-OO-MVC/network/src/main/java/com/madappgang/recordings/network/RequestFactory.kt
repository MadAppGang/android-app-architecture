/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 6/9/18.
 */

package com.madappgang.recordings.network

import com.madappgang.recordings.core.Foldable
import com.madappgang.recordings.core.Folder
import com.madappgang.recordings.core.Id
import com.madappgang.recordings.core.Track
import com.madappgang.recordings.network.mapper.NetworkMapper

internal class RequestFactory(private val endpoint: Endpoint, private val mapper: NetworkMapper) {

    /**
     * @throws IllegalStateException if [requestType] is not supported
     */
    fun <T> makeForFetching(requestType: Class<T>, id: Id) = when (requestType) {
        Folder::class.java,
        Track::class.java -> makeForFetchingFoldable(id)

        else -> throw IllegalArgumentException()
    }

    /**
     * @throws IllegalStateException if [requestType] or [fetchingOptions] is not supported
     */
    fun <T> makeForFetching(requestType: Class<T>, fetchingOptions: FetchingOptions) =
        when (requestType) {
            Folder::class.java,
            Track::class.java -> makeForFetchingFoldable(fetchingOptions)

            else -> throw IllegalArgumentException()
        }

    /**
     * @throws IllegalStateException if type [entity] is not supported
     */
    fun <T> makeForCreate(entity: T): Request {
        return when (entity) {
            is Foldable,
            is Track -> makeForCreateFoldable(entity as Foldable)

            else -> throw IllegalArgumentException()
        }
    }

    /**
     * @throws IllegalStateException if type [entity] is not supported
     */
    fun <T> makeForRemove(entity: T) = when (entity) {
        is Folder -> makeForRemoveFoldable(entity.id)
        is Track -> makeForRemoveFoldable(entity.id)

        else -> throw IllegalArgumentException()
    }

    fun makeForDownload(path: String) =
        Request(buildUrl(endpoint, "api/download/$path"), RequestMethod.GET)

    private fun makeForFetchingFoldable(id: Id) =
        Request(buildUrl(endpoint, "api/foldable/${id.value}"), RequestMethod.GET)

    private fun makeForFetchingFoldable(fetchingOptions: FetchingOptions): Request {
        val ownerId = fetchingOptions.options.firstOrNull { it is Constraint.OwnerId }

        val isConstraintExist =
            fetchingOptions.options.contains(Constraint.Owner(Folder::class.java))

        return if (isConstraintExist && ownerId != null) {
            val pathSegment = "api/foldable/${ownerId.value}/contents"
            Request(buildUrl(endpoint, pathSegment), RequestMethod.GET)
        } else {
            throw IllegalArgumentException()
        }
    }

    private fun makeForCreateFoldable(foldable: Foldable): Request {
        val dataParts = mutableListOf<DataPart<*>>()
        dataParts.add(DataPart.JsonPart("body", mapper.toJson(foldable)))

        if (foldable is Track) {
            dataParts.add(DataPart.AudioPart("track", foldable.name))
        }

        return Request(buildUrl(endpoint, "api/foldable"), RequestMethod.POST, dataParts)
    }


    private fun makeForRemoveFoldable(id: Id?) = id?.let {
        Request(buildUrl(endpoint, "api/foldable/${id.value}"), RequestMethod.DELETE)
    } ?: let {
        throw IllegalArgumentException()
    }


}

private fun RequestFactory.buildUrl(
    endpoint: Endpoint,
    pathSegment: String
): String {
    return "$endpoint/$pathSegment"
}