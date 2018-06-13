/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 6/13/18.
 */

package com.madappgang.recordings.network

import com.madappgang.recordings.core.Id

class FetchingOptions {
    val options = mutableSetOf<Constraint>()
}

sealed class Constraint(val value: Any) {

    class OwnerId(id: Id): Constraint(id)

    class Owner(clazz: Class<*>): Constraint(clazz)

     override fun equals(other: Any?): Boolean {
         if (this === other) return true
         if (javaClass != other?.javaClass) return false

         other as Constraint

         if (value != other.value) return false

         return true
     }

     override fun hashCode(): Int {
         return value.hashCode()
     }
 }

fun FetchingOptions.add(constraint: Constraint): FetchingOptions {
    options.add(constraint)
    return this
}