/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 6/8/18.
 */

package com.madappgang.recordings.core

import android.os.Parcel
import android.os.Parcelable

data class Track(var id: Id? = null) : Parcelable {

    var name: String = ""
    var url: String = ""

    constructor(parcel: Parcel) : this(parcel.readParcelable<Id>(Id::class.java.classLoader)) {
        name = parcel.readString()
        url = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(id, flags)
        parcel.writeString(name)
        parcel.writeString(url)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<Track> {

        override fun createFromParcel(parcel: Parcel) = Track(parcel)

        override fun newArray(size: Int) = arrayOfNulls<Track?>(size)

    }
}