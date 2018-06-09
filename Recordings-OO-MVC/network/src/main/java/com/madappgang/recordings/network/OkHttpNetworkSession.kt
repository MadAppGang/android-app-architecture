/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 6/9/18.
 */

package com.madappgang.recordings.network

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

class OkHttpNetworkSession : NetworkSession {

    private val client: OkHttpClient by lazy { OkHttpClient().newBuilder().build() }

    override fun makeRequest(request: Request, completionHandler: (Response?, Throwable?) -> Unit) {
        try {
            val call = client.newCall(request)
            val response = call.execute()
            completionHandler.invoke(response, null)
        } catch (e: Throwable) {
            completionHandler.invoke(null, e)
        }
    }
}