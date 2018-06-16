/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 6/13/18.
 */

package com.madappgang.recordings.network

class FetchingOptions {

    val options = mutableSetOf<Constraint<*>>()
}

sealed class Constraint<T>(val value: T) {

    class FoldablePath(value: String): Constraint<String>(value)

    class FoldableName(value: String): Constraint<String>(value)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Constraint<*>) return false

        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        return value?.hashCode() ?: 0
    }

 }

fun FetchingOptions.add(constraint: Constraint<*>): FetchingOptions {
    options.add(constraint)
    return this
}