package com.madappgang.madappgangmvvmtestarch.utils

import com.madappgang.madappgangmvvmtestarch.utils.Result.Error

/**
 * Created by Serhii Chaban sc@madappgang.com on 30.05.18.
 */
sealed class Result<out T, out E> {
    data class Success<out T>(val value: T) : Result<T, Nothing>()
    data class Error<out E>(val error: E) : Result<Nothing, E>()
}
