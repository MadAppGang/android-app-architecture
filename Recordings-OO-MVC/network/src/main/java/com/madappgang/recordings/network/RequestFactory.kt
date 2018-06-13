/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 6/9/18.
 */

package com.madappgang.recordings.network

import com.google.gson.Gson
import com.madappgang.recordings.core.Folder
import com.madappgang.recordings.core.Id
import com.madappgang.recordings.core.Track

internal class RequestFactory(
    private val endpoint: Endpoint,
    private val gson: Gson
) {

    fun <T> makeForFetching(clazz: Class<T>, id: Id) = when (clazz) {
        Folder::class.java,
        Track::class.java -> makeForFetchingFoldable(id)

        else -> throw IllegalArgumentException()
    }

    fun <T> makeForFetching(clazz: Class<T>, fetchingOptions: FetchingOptions) = when (clazz) {
        Folder::class.java,
        Track::class.java -> makeForFetchingFoldable(fetchingOptions)

        else -> throw IllegalArgumentException()
    }

    fun <T> makeForCreate(entity: T): Request {
        val data = gson.toJson(entity)
        return when (entity) {
            is Folder,
            is Track -> makeForCreateFoldable(data)

            else -> throw IllegalArgumentException()
        }
    }

    fun <T> makeForRemove(entity: T) = when (entity) {
        is Folder -> makeForRemoveFoldable(entity.id)
        is Track -> makeForRemoveFoldable(entity.id)

        else -> throw IllegalArgumentException()
    }

    fun makeForDownload(url: String) =
        Request(url, RequestMethod.GET)

    private fun makeForFetchingFoldable(id: Id) =
        Request(buildUrl(endpoint, "api/foldable/${id.id}"), RequestMethod.GET)

    private fun makeForFetchingFoldable(fetchingOptions: FetchingOptions): Request {
        val ownerId = fetchingOptions.options.firstOrNull { it is Constraint.OwnerId }

        return if (fetchingOptions.options.contains(Constraint.Owner(Folder::class.java)) &&
            ownerId != null
        ) {
            Request(buildUrl(endpoint, "api/foldable/${ownerId.value}/contents"), RequestMethod.GET)
        } else {
            throw IllegalArgumentException()
        }
    }

    private fun makeForCreateFoldable(data: String) =
        Request(buildUrl(endpoint, "api/foldable"), RequestMethod.POST, data)


    private fun makeForRemoveFoldable(id: Id?) = id?.let {
        Request(buildUrl(endpoint, "api/foldable/${id.id}"), RequestMethod.DELETE)
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