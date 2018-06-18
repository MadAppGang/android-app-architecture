/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 6/13/18.
 */

package com.madappgang.recordings.network.mapper

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.madappgang.recordings.core.Foldable
import com.madappgang.recordings.core.Folder
import com.madappgang.recordings.core.Track
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

internal class NetworkMapper {

    private val gson = createGson()

    fun <T> mapToEntity(data: String, entityType: Class<T>) = gson.fromJson(data, entityType)

    fun <T> mapToList(data: String, entityType: Class<T>): List<T> =
        gson.fromJson(data, ListOf(entityType))

    fun toJson(entity: Any) = gson.toJson(entity)
}

internal fun createGson() = GsonBuilder()
    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
    .registerTypeAdapter(Foldable::class.java, FoldableMapper())
    .registerTypeAdapter(Folder::class.java, FolderMapper())
    .registerTypeAdapter(Track::class.java, TrackMapper())
    .create()

internal class ListOf<T>(private val type: Class<T>) : ParameterizedType {

    override fun getRawType() = List::class.java

    override fun getOwnerType() = null

    override fun getActualTypeArguments() = arrayOf<Type>(type)

}