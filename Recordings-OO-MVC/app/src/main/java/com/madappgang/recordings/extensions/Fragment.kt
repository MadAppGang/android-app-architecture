/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 6/10/18.
 */

package com.madappgang.recordings.extensions

import android.support.v4.app.Fragment
import java.lang.IllegalArgumentException

/**
 * @throws [IllegalArgumentException] when type [defaultValue] is not supported
 */
internal fun <T> Fragment.getArgument(key: String, defaultValue: T): T {
    return when (defaultValue) {
        is String? -> arguments?.getString(key) as T ?: defaultValue
        is Int? -> arguments?.getInt(key) as T ?: defaultValue

        else -> {
            val valueType = defaultValue?.let { (defaultValue as Any)::class.java }
            val message = "Type of defaultValue $valueType is not supported"

            throw  IllegalArgumentException(message)
        }
    }
}