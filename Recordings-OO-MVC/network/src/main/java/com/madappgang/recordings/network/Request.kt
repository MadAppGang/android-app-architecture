/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 6/13/18.
 */

package com.madappgang.recordings.network

class Request(
    var path: String,
    var requestMethod: RequestMethod,
    var body: String = ""
)

enum class RequestMethod {
    GET,
    POST,
    PATCH,
    DELETE,
    PUT
}