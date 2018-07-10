/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 6/13/18.
 */

package com.madappgang.recordings.network

open class Request(
    var path: String,
    var requestMethod: RequestMethod,
    var dataParts: List<DataPart<*>> = emptyList()
)


sealed class DataPart<T>(val partName: String, val value: T, val bodyType: BodyType) {

    class JsonPart(partName: String = "body", value: String) :
        DataPart<String>(partName, value, BodyType.JSON)

    class AudioPart(partName: String = "body", value: String) :
        DataPart<String>(partName, value, BodyType.JSON)

}

enum class BodyType(val type: String) {
    JSON("application/json; charset=utf-8"),
    FILE("audio/mp4")
}

enum class RequestMethod {
    GET,
    POST,
    PATCH,
    DELETE,
    PUT
}