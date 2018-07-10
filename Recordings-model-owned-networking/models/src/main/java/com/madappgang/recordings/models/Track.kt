/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 7/9/18.
 */

package com.madappgang.recordings.models

import android.os.Parcelable
import com.madappgang.recordings.network.BodyPart
import com.madappgang.recordings.network.Request
import com.madappgang.recordings.network.RequestMethod
import com.madappgang.recordings.network.Result
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Track(
    override var path: String,
    override var name: String
) : Foldable(), Parcelable {

    fun save(folder: Folder, trackName: String): Result<Void> {
        val track = Track(folder.fullPath, trackName)
        val dataParts = listOf(
            BodyPart.Json("body", mapper.toJson(track)),
            BodyPart.Audio("track", fullPath)
        )

        val request = Request.MultipartBody(
            Void::class.java,
            "api/track",
            RequestMethod.POST,
            bodyParts = dataParts
        )
        return Foldable.network.executeQuery(request, mapper)
    }

    override fun remove(): Result<Void> {
        val request = Request.SingleBody(
            Void::class.java,
            "api/track",
            RequestMethod.DELETE,
            queryParams = mutableMapOf("path" to fullPath)
        )
        return Foldable.network.executeQuery(request, mapper)
    }
}