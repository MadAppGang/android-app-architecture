/*
 * Copyright 2018 MadAppGang.
 *
 * Created by Andrii Fedorov afedorov@madappgang.com on 6/8/18.
 */

package com.madappgang.recordings.core

sealed class Result<T> {

    class Success<T>(val value: T) : Result<T>()

    class Failure<T>(val throwable: Throwable) : Result<T>()

}