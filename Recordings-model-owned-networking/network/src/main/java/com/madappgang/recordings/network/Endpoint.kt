/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 7/10/18.
 */

package com.madappgang.recordings.network

sealed class Endpoint(val scheme: String, val host: String, val port: Int) {

    object Staging: Endpoint("http", "localhost", 8000)

    override fun toString(): String {
        return "$scheme://$host:$port"
    }

}