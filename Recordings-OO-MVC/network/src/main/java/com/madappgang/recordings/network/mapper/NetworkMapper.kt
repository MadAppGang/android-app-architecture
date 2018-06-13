/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 6/13/18.
 */

package com.madappgang.recordings.network.mapper

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

internal class NetworkMapper(private val gson: Gson) {

    fun <T> mapToEntity(data: String, entityType: Class<T>) = gson.fromJson(data, entityType)

    fun <T> mapToList(data: String) =
        gson.fromJson<List<T>>(data, object : TypeToken<List<T>>(){}.type)

}