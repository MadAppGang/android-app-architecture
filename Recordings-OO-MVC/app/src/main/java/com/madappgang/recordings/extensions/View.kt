/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 6/10/18.
 */

package com.madappgang.recordings.extensions

import android.view.View

internal fun View.makeVisible() {
    visibility = View.VISIBLE
}

internal fun View.makeGone() {
    visibility = View.GONE
}

internal fun View.makeInvisible() {
    visibility = View.INVISIBLE
}