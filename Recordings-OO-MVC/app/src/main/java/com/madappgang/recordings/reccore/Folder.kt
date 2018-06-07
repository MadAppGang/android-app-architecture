package com.madappgang.recordings.reccore

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Andrii Fedorov afedorov@madappgang.com on 6/7/18.
 */

data class Folder(var id: String = ""): Foldable, Parcelable {
    var name: String = ""

    constructor(parcel: Parcel) : this(parcel.readString()) {
        name = parcel.readString()
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(id)
        dest?.writeString(name)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<Folder> {
        override fun createFromParcel(parcel: Parcel): Folder {
            return Folder(parcel)
        }

        override fun newArray(size: Int): Array<Folder?> {
            return arrayOfNulls(size)
        }
    }


}