/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 7/10/18.
 */

package com.madappgang.recordings.network

sealed class Request<T>(
    var resultType: Class<T>,
    var pathSegment: String,
    var requestMethod: RequestMethod,
    var queryParams: Map<String, String?> = mutableMapOf()
) {

    class SingleBody<T>(
        resultType: Class<T>,
        pathSegment: String,
        requestMethod: RequestMethod,
        queryParams: Map<String, String?> = mutableMapOf(),
        var body: BodyPart<*> = BodyPart.Json(value = "")
    ) : Request<T>(resultType, pathSegment, requestMethod, queryParams)

    class MultipartBody<T>(
        resultType: Class<T>,
        pathSegment: String,
        requestMethod: RequestMethod,
        queryParams: Map<String, String?> = mutableMapOf(),
        var bodyParts: List<BodyPart<*>> = emptyList()
    ) : Request<T>(resultType, pathSegment, requestMethod, queryParams)
}

sealed class BodyPart<T>(val partName: String, val value: T, val bodyMimeType: BodyMimeType) {

    class Json(partName: String = "body", value: String) :
        BodyPart<String>(partName, value, BodyMimeType.JSON)

    class Audio(partName: String = "body", path: String) :
        BodyPart<String>(partName, path, BodyMimeType.JSON)

}

enum class BodyMimeType(val type: String) {
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