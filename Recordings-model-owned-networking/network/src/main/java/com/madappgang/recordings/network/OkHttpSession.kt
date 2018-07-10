/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 7/10/18.
 */

package com.madappgang.recordings.network

import okhttp3.*
import java.io.File

internal class OkHttpSession(
    private val endpoint: Endpoint,
    private val client: OkHttpClient = buildClient()
) : NetworkSession {

    override fun <T, R : Response<T>> makeRequest(request: Request<*>, requestType: Class<R>): R {
        val call = client.newCall(convertRequest(request))
        val response = call.execute()

        return convertResponse(response, requestType)
    }

    private fun convertRequest(request: Request<*>): okhttp3.Request {
        val builder = when (request.requestMethod) {
            RequestMethod.GET -> okhttp3.Request.Builder().get()
            RequestMethod.POST -> okhttp3.Request.Builder().post(buildBody(request))
            RequestMethod.DELETE -> okhttp3.Request.Builder().delete()
            RequestMethod.PUT -> okhttp3.Request.Builder().put(buildBody(request))
            RequestMethod.PATCH -> okhttp3.Request.Builder().patch(buildBody(request))
        }

        builder.url(buildUrl(request))
        return builder.build()
    }

    private fun buildBody(request: Request<*>) = when (request) {
        is Request.SingleBody -> buildRequestBody(request.body)
        is Request.MultipartBody -> buildMultipartRequestBody(request)
    }

    private fun buildRequestBody(bodyPart: BodyPart<*>): RequestBody {
        val mediaType = MediaType.parse(bodyPart.bodyMimeType.type)
        return when (bodyPart) {
            is BodyPart.Json -> RequestBody.create(mediaType, bodyPart.value)
            is BodyPart.Audio -> RequestBody.create(mediaType, File(bodyPart.value))
        }
    }

    private fun buildMultipartRequestBody(request: Request.MultipartBody<*>): RequestBody {
        val multipartBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)

        request.bodyParts.forEach {
            val body = buildRequestBody(it)
            multipartBody.addFormDataPart(it.partName, it.partName, body)
        }

        return multipartBody.build()
    }

    private fun buildUrl(request: Request<*>): HttpUrl {
        val url = HttpUrl.Builder()
            .scheme(endpoint.scheme)
            .host(endpoint.host)
            .port(endpoint.port)
            .addPathSegments(request.pathSegment)

        request.queryParams.forEach { name, value -> url.addQueryParameter(name, value) }

        return url.build()
    }

    private fun <T, R : Response<T>> convertResponse(
        response: okhttp3.Response,
        requestType: Class<R>
    ): R {
        val body = response.body()
        val statusCode = response.code()

        @Suppress("UNCHECKED_CAST")
        return when (requestType) {
            Response.Body::class.java -> Response.Body(statusCode, body.toString()) as R
            Response.BufferedBody::class.java ->
                Response.BufferedBody(statusCode, body!!.source()) as R

            else -> {
                throw IllegalArgumentException("Argument requestType $requestType is not supported")
            }
        }
    }
}

private fun buildClient(): OkHttpClient {
    return OkHttpClient().newBuilder().build()
}