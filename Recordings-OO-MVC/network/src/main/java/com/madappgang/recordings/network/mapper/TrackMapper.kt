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
                addProperty("path", path)
                addProperty("name", name)
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
        val path = if (jsonObject.has("path")) jsonObject["path"].asString else ""
        val name = if (jsonObject.has("name")) jsonObject["name"].asString else ""

        return Track(name = name, path = path)
    }
}
