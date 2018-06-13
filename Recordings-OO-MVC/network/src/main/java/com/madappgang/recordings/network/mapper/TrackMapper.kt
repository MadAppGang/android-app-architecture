/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 6/13/18.
 */

package com.madappgang.recordings.network.mapper

import com.google.gson.*
import com.madappgang.recordings.core.Id
import com.madappgang.recordings.core.Track
import java.lang.reflect.Type

internal class TrackMapper : JsonSerializer<Track>, JsonDeserializer<Track> {

    override fun serialize(
        src: Track?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ) = src?.let {
        JsonObject().apply {
            with(src) {
                id?.let { addProperty("id", it.id) }
                folderId?.let { addProperty("folder_id", it.id) }
                addProperty("name", name)
                addProperty("path", path)
                addProperty("type", FileType.TRACK.type)
            }
        }
    } ?: let {
        JsonObject()
    }

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Track {
        val jsonObject = json as JsonObject
        val id = if (jsonObject.has("id")) Id(jsonObject["id"].asString) else null
        val folderId = if (jsonObject.has("folder_Id")) {
                Id(jsonObject["folder_Id"].asString)
            } else {
                null
            }
        val name = if (jsonObject.has("name")) jsonObject["name"].asString else ""
        val path = if (jsonObject.has("path")) jsonObject["path"].asString else ""

        return Track(
            id = id,
            folderId = folderId,
            name = name,
            path = path
        )
    }
}
