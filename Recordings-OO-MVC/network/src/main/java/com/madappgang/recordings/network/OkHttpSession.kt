/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 6/9/18.
 */

package com.madappgang.recordings.network

import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import java.io.File

internal class OkHttpSession(
    private val client: OkHttpClient = OkHttpClient().newBuilder().build()
) : NetworkSession {

    override fun <T, R : Response<T>> makeRequest(request: Request, requestType: Class<R>): R {
        val call = client.newCall(convertRequest(request))
        val response = call.execute()

        return convertResponse(response, requestType)
    }

    private fun <T, R : Response<T>> convertResponse(
        response: okhttp3.Response,
        clazz: Class<R>
    ): R {
        val classBody = Response.Body::class.java
        val classBufferedBody = Response.BufferedBody::class.java

        val body = response.body()
        val statusCode = response.code()

        return when (clazz) {
            classBody -> {
                Response.Body(statusCode, body.toString()) as R
            }

            classBufferedBody -> {
                body?.let {
                    Response.BufferedBody(statusCode, it.source()) as R
                } ?: let {
                    throw NullPointerException()
                }
            }

            else -> {
                throw IllegalArgumentException("Argument clazz $clazz is not supported")
            }
        }
    }

    private fun convertRequest(request: Request): okhttp3.Request {
        val requestBody = if (request.dataParts.size == 1) {
            buildRequestBody(request.dataParts.first())
        } else {
            buildMultipartRequestBody(request)
        }

        val builder = when (request.requestMethod) {
            RequestMethod.GET -> okhttp3.Request.Builder().get()
            RequestMethod.POST -> okhttp3.Request.Builder().post(requestBody)
            RequestMethod.DELETE -> okhttp3.Request.Builder().delete()
            RequestMethod.PUT -> okhttp3.Request.Builder().put(requestBody)
            RequestMethod.PATCH -> okhttp3.Request.Builder().patch(requestBody)
        }

        builder.url(request.path)
        return builder.build()
    }

    private fun buildMultipartRequestBody(request: Request): RequestBody {
        val multipartBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
        request.dataParts.forEach {
            val body = buildRequestBody(it)
            multipartBody.addFormDataPart(it.partName, it.partName, body)
        }

        return multipartBody.build()
    }

    private fun buildRequestBody(dataPart: DataPart<*>): RequestBody {
        val mediaType = MediaType.parse(dataPart.bodyType.type)
        return when (dataPart) {
            is DataPart.JsonPart -> RequestBody.create(mediaType, dataPart.value)
            is DataPart.AudioPart -> RequestBody.create(mediaType, File(dataPart.value))
        }
    }
}

