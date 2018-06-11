/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 6/9/18.
 */

package com.madappgang.recordings.network

sealed class Endpoint(val scheme: String, val host: String, val port: String) {

    object Staging: Endpoint("http", "localhost", "8000")

}