/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 6/8/18.
 */

package com.madappgang.recordings.core

interface Foldable {
    var path: String
    var name: String

    fun getFullPath() = "$path/$name"
}