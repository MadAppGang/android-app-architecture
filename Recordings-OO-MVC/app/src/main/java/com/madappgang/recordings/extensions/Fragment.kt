/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 6/10/18.
 */

package com.madappgang.recordings.extensions

import android.support.v4.app.Fragment
import java.lang.IllegalArgumentException

fun <T: Any> Fragment.getArgument(key: String, defaultValue: T?): T {
    return when (defaultValue) {
        is String -> (arguments?.getString(key, defaultValue) ?: defaultValue) as T
        is Int -> (arguments?.getInt(key, defaultValue) ?: defaultValue) as T

        else -> {
            val valueType = defaultValue?.let { it::class.java.simpleName }
            val message = "Type of defaultValue $valueType is not Supported"
            throw  IllegalArgumentException(message)
        }
    }
}
