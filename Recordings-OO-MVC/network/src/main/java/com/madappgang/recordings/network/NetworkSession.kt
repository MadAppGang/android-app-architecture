/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 6/9/18.
 */

package com.madappgang.recordings.network

interface NetworkSession {

    fun <T, R: Response<T>> makeRequest(request: Request, requestType: Class<R>) : R

}
