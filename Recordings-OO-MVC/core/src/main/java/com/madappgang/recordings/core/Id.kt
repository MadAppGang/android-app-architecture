/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 6/9/18.
 */

package com.madappgang.recordings.core

import android.os.Parcel
import android.os.Parcelable

data class Id(val id: String) : Parcelable {

    constructor(parcel: Parcel) : this(parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<Id> {

        override fun createFromParcel(parcel: Parcel) = Id(parcel)

        override fun newArray(size: Int) = arrayOfNulls<Id?>(size)

    }
}