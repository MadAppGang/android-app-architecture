/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 6/13/18.
 */

package com.madappgang.recordings.network.mapper

import com.google.gson.*
import com.google.gson.reflect.TypeToken
import com.madappgang.recordings.core.Foldable
import com.madappgang.recordings.core.Folder
import com.madappgang.recordings.network.NetworkExceptions
import com.madappgang.recordings.core.Track
import java.lang.reflect.Type

internal class FoldableMapper : JsonDeserializer<Foldable>, JsonSerializer<Foldable> {

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Foldable {
        val jsonObject = json as JsonObject
        val type = FileType.ofValue(jsonObject["type"].asString)

        return if (type == FileType.FOLDER) {
            context?.deserialize<Folder>(jsonObject, object : TypeToken<Folder>() {}.type) ?: run {
                throw NetworkExceptions.ObjectParsingException()
            }
        } else {
            context?.deserialize<Track>(jsonObject, object : TypeToken<Track>() {}.type) ?: run {
                throw NetworkExceptions.ObjectParsingException()
            }
        }
    }

    override fun serialize(
        src: Foldable?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ) = context?.let {
        when (src) {
            is Folder -> it.serialize(src, object : TypeToken<Folder>() {}.type)
            is Track -> it.serialize(src, object : TypeToken<Track>() {}.type)

            else -> {
                val srcType = src?.javaClass?.simpleName ?: ""
                throw IllegalArgumentException("Serializer for type $srcType not implemented")
            }
        }
    } ?: let {
        JsonObject()
    }
}

internal enum class FileType(val type: String) {
    FOLDER("folder"),
    TRACK("track");

    companion object {
        fun ofValue(type: String): FileType {
            return when (type) {
                FOLDER.type -> FOLDER
                TRACK.type -> TRACK
                else -> {
                    throw IllegalArgumentException("Type $type not supported")
                }
            }
        }
    }
}