/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 7/9/18.
 */

package com.madappgang.recordings.models

import android.os.Parcelable
import com.madappgang.recordings.network.*
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Folder(
    override var path: String = "",
    override var name: String = ""
) : Foldable(), Parcelable {

    companion object {

        fun fetchRoot(): Result<Folder> {
            val request = Request.SingleBody(
                Folder::class.java,
                "api/folder",
                RequestMethod.GET
            )

            return Foldable.network.executeQuery(request, mapper)
        }
    }

    fun create(): Result<Folder> {
        val request = Request.SingleBody(
            Folder::class.java,
            "api/folder",
            RequestMethod.POST,
            body = BodyPart.Json(value = mapper.toJson(this))
        )
        return Foldable.network.executeQuery(request, mapper)
    }

    override fun remove(): Result<Void> {
        val request = Request.SingleBody(
            Void::class.java,
            "api/folder",
            RequestMethod.DELETE,
            queryParams = mutableMapOf("path" to fullPath)
        )
        return Foldable.network.executeQuery(request, mapper)
    }

    fun fetchContent(): Result<List<Foldable>> {
        val request = Request.SingleBody(
            emptyList<Foldable>().javaClass,
            "api/folder",
            RequestMethod.GET,
            queryParams = mutableMapOf("path" to fullPath)
        )

        return Foldable.network.executeQuery(request, mapper)
    }
}