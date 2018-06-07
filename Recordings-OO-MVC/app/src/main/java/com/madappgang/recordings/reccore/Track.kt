package com.madappgang.recordings.reccore

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Andrii Fedorov afedorov@madappgang.com on 6/7/18.
 */

data class Track(var id: String = ""): Parcelable {
    var name: String = ""
    var path: String = ""
    var url: String = ""

    constructor(parcel: Parcel) : this(parcel.readString()) {
        name = parcel.readString()
        path = parcel.readString()
        url = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(path)
        parcel.writeString(url)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<Track> {
        override fun createFromParcel(parcel: Parcel): Track {
            return Track(parcel)
        }

        override fun newArray(size: Int): Array<Track?> {
            return arrayOfNulls(size)
        }
    }
}