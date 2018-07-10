/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 7/9/18.
 */

package com.madappgang.recordings.network

sealed class Result<out T> {

    data class Success<T>(val value: T) : Result<T>()

    data class Failure(val error: Throwable) : Result<Nothing>()

}
