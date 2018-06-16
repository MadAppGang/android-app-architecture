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
     * @throws IllegalStateException if [entityType] or [fetchingOptions] is not supported
     */
    fun <T> makeForFetching(entityType: Class<T>, fetchingOptions: FetchingOptions) =
        when (entityType) {
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
        is Foldable -> makeForRemoveFoldable(entity.name)

        else -> throw IllegalArgumentException()
    }

    private fun makeForFetchingFoldable(fetchingOptions: FetchingOptions): Request {
        val path = fetchingOptions.options
            .firstOrNull { it is Constraint.FoldablePath } as Constraint.FoldablePath?
            ?: throw IllegalArgumentException()

        val name = fetchingOptions.options
            .firstOrNull { it is Constraint.FoldableName } as Constraint.FoldableName?

        return if (path.value.isEmpty()) {
            Request(buildUrl(endpoint, "api/foldable/"), RequestMethod.GET)
        } else {
            val dataParts = mutableListOf<DataPart<*>>()
            name?.let { dataParts.add(DataPart.JsonPart(value = it.value)) }

            Request(path.value, RequestMethod.GET, dataParts)
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


    private fun makeForRemoveFoldable(path: String) = if (path.isEmpty())let {
        Request(path, RequestMethod.DELETE)
    } else {
        throw IllegalArgumentException()
    }


}

private fun RequestFactory.buildUrl(
    endpoint: Endpoint,
    pathSegment: String
): String {
    return "$endpoint/$pathSegment"
}