/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 7/10/18.
 */

package com.madappgang.recordings.network

sealed class NetworkExceptions(message: String) : Throwable(message) {

    class NotFound : NetworkExceptions("Not Found")

    class BadRequest : NetworkExceptions("Bad Request")

    class UnknownException(val statusCode: Int) :
        NetworkExceptions("Unknown Exception. Status code $statusCode")

}