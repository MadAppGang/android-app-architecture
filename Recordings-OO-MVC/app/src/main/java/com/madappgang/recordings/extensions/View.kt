/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 6/10/18.
 */

package com.madappgang.recordings.extensions

import android.content.res.Resources
import android.util.TypedValue
import android.view.View

fun View.makeVisible() {
    visibility = View.VISIBLE
}

fun View.makeGone() {
    visibility = View.GONE
}

fun View.makeInvisible() {
    visibility = View.INVISIBLE
}

fun View.getPxFromDp(dp: Float): Float {
    val r: Resources = this.context.resources
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.displayMetrics)
}

fun View.formatMilliseconds(milliseconds: Int): String {
    val seconds = (milliseconds / 1000) % 60
    val minutes = (milliseconds / (1000 * 60) % 60)
    val hours = (milliseconds / (1000 * 60 * 60) % 24)
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}