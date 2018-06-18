/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 6/13/18.
 */

package com.madappgang.recordings.network.mapper

import com.google.gson.*
import com.madappgang.recordings.core.Folder
import com.madappgang.recordings.core.Id
import java.lang.reflect.Type

internal class FolderMapper : JsonSerializer<Folder>, JsonDeserializer<Folder> {

    override fun serialize(
        src: Folder?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ) = src?.let {
        JsonObject().apply {
            with(src) {
                addProperty("path", path)
                addProperty("name", name)
                addProperty("type", FileType.FOLDER.type)
            }
        }
    } ?: let {
        JsonObject()
    }

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Folder {
        val jsonObject = json as JsonObject
        val path = if (jsonObject.has("path")) jsonObject["path"].asString else ""
        val name = if (jsonObject.has("name")) jsonObject["name"].asString else ""

        return Folder(path = path, name = name)
    }
}