/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 6/9/18.
 */

package com.madappgang.recordings.network

import okhttp3.Request
import okhttp3.Response

interface NetworkSession {

    fun makeRequest(request: Request, completionHandler: (Response?, Throwable?) -> Unit)

}
