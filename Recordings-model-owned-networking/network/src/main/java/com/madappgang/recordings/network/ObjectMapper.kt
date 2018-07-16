/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 7/11/18.
 */

package com.madappgang.recordings.network

interface ObjectMapper {

    fun <T> toObject(data: String, objectType: Class<T>): T

    fun <T> toJson(entity: T): String

}