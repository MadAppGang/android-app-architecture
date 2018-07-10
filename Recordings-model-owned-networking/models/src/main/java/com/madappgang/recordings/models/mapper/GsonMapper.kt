/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 7/10/18.
 */

package com.madappgang.recordings.models.mapper

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.madappgang.recordings.models.Foldable
import com.madappgang.recordings.models.Folder
import com.madappgang.recordings.models.Track
import com.madappgang.recordings.network.ObjectMapper

class GsonMapper : ObjectMapper {

    private val gson = createGson()

    override fun <T> toObject(data: String, objectType: Class<T>) = gson.fromJson(data, objectType)

    override fun <T> toJson(entity: T) = gson.toJson(entity)

}

internal fun createGson() = GsonBuilder()
    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
    .registerTypeAdapter(Foldable::class.java, FoldableMapper())
    .registerTypeAdapter(Folder::class.java, FolderMapper())
    .registerTypeAdapter(Track::class.java, TrackMapper())
    .create()