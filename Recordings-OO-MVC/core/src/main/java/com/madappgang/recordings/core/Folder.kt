/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 6/8/18.
 */

package com.madappgang.recordings.core

import android.os.Parcel
import android.os.Parcelable

data class Folder(var id: Id? = null) : Foldable, Parcelable {

    var parentId: Id? = null
    var name: String = ""

    constructor(parcel: Parcel) : this(parcel.readParcelable<Id>(Id::class.java.classLoader)) {
        parentId = parcel.readParcelable(Id::class.java.classLoader)
        name = parcel.readString()
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeParcelable(id, flags)
        dest?.writeParcelable(parentId, flags)
        dest?.writeString(name)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<Folder> {

        override fun createFromParcel(parcel: Parcel) = Folder(parcel)

        override fun newArray(size: Int) = arrayOfNulls<Folder?>(size)

    }
}