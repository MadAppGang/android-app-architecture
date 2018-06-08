package com.madappgang.recordings.network

/**
 * Created by Andrii Fedorov afedorov@madappgang.com on 6/8/18.
 */

class Result<T> private constructor(private val result: Any?) {
    companion object {
        fun <T> success(value: T): Result<T> = Result(value)
        fun <T> failure(exception: Throwable) = Result<T>(Failure(exception))
    }

    val isFailure: Boolean get() = result is Failure
    val isSuccess: Boolean get() = result !is Failure

    fun get(): T =
            if (result is Failure) throw result.exception
            else result as T

    fun getOrNull(): T? =
            if (result is Failure) null
            else result as T

    fun exceptionOrNull(): Throwable? =
            (result as? Failure)?.exception

    fun onTerminate(block: (Unit) -> Unit): Result<T> {
        block(kotlin.Unit)
        return this
    }

    fun onFailure(block: (Throwable) -> Unit): Result<T> {
        if (isFailure) block((result as Failure).exception)
        return this
    }

    fun onSuccess(block: (T) -> Unit): Result<T> {
        if (isSuccess) block(result as T)
        return this
    }

    private class Failure(@JvmField val exception: Throwable)
}