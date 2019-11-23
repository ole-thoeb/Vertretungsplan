package com.example.eloem.vertretungsplan.util

import com.example.eloem.vertretungsplan.util.Result.Failure
import com.example.eloem.vertretungsplan.util.Result.Success
import java.lang.RuntimeException
import java.util.ArrayList

sealed class Result<out T, out E: Any> {

    data class Success<T, E: Any>(val value: T): Result<T, E>()

    data class Failure<T, E: Any>(val error: E): Result<T, E>()
}

/**
 * supplies an alternative value dependent on the error to use if this is [Failure]
 */
inline fun <T, E: Any> Result<T, E>.onFailure(action: (E) -> T): T = when(this) {
    is Failure -> action(error)
    is Success -> value
}

/**
 * supplies an alternative value to use if this is [Failure]
 */
fun <T, E: Any> Result<T, E>.orElse(other: T): T = when(this) {
    is Failure -> other
    is Success -> value
}

/**
 * executes [action] if this is [Success] otherwise returns the [Failure]
 */
inline fun <T, E: Any, S> Result<T, E>.withSuccess(action: (T) -> S): Result<S, E> = when(this) {
    is Failure -> Failure(error)
    is Success -> Success(action(value))
}

/**
 * executes [action] if this is [Failure] otherwise returns the [Success]
 */
inline fun <T, E: Any, E2: Any> Result<T, E>.withFailure(action: (E) -> E2): Result<T, E2> = when(this) {
    is Failure -> Failure(action(error))
    is Success -> Success(value)
}

/**
 * executes [action] if this is [Success] and returns the new [Result] otherwise returns the [Failure]
 */
inline fun <T, E: E2, T2, E2: Any> Result<T, E>.chainSuccess(
        action: (T) -> Result<T2, E2>
): Result<T2, E2> = when(this) {
    is Failure -> Failure(error)
    is Success -> action(value)
}

/**
 * throws the error if this is [Failure]
 */
fun <T, E: Any> Result<T, E>.throwError(): T = when(this) {
    is Failure -> when(error) {
        is Throwable -> throw error
        else -> throw RuntimeException(error.toString())
    }
    is Success -> value
}
/**
 * executes [action] if this is [Failure]
 */
fun <T, E: Any> Result<T, E>.ifFailure(action: (E) -> Unit): Result<T, E> = when(this) {
    is Failure -> {
        action(error)
        this
    }
    is Success -> this
}

/**
 * executes [action] if this is [Success]
 */
fun <T, E: Any> Result<T, E>.ifSuccess(action: (T) -> Unit): Result<T, E> = when(this) {
    is Failure -> this
    is Success -> {
        action(value)
        this
    }
}

data class TryResult<T>(val tryBlock: () -> T)

fun <T> tryResult(tryBlock: () -> T) = TryResult(tryBlock)

inline fun <T, E: Any>TryResult<T>.catchResult(catchBlock: (Exception) -> E): Result<T, E> {
    return try {
        Success(tryBlock())
    } catch (e: Exception) {
        Failure(catchBlock(e))
    }
}

fun <T>TryResult<T>.catchDefault(): Result<T, Exception> {
    return try {
        Success(tryBlock())
    } catch (e: Exception) {
        Failure(e)
    }
}

inline fun <T, E: Any>tryCatchResult(tryBlock: () -> T, catchBlock: (Exception) -> E): Result<T, E> {
    return try {
        Success(tryBlock())
    } catch (e: Exception) {
        Failure(catchBlock(e))
    }
}

inline fun <T> T?.ifNull(producer: () -> T): T {
    return this ?: producer()
}

inline fun <T> T?.onNull(block: (T?) -> Unit): T? {
    if (this == null) block(this)
    return this
}

fun <T1, T2, E: Any, R> Result<T1, E>.binaryWith(other: Result<T2, E>, action:(T1, T2) -> R): Result<R, E> {
    return when (this) {
        is Failure -> Failure(error)
        is Success -> when (other) {
            is Failure -> Failure(other.error)
            is Success -> Success(action(value, other.value))
        }
    }
}

inline fun <T, R, E: Any>List<T>.mapOptional(transform: (T) -> Result<R, E>): Result<List<R>, E> {
    val destination = ArrayList<R>(size)
    for (item in this) {
        when(val result = transform(item)) {
            is Failure -> return Failure(result.error)
            is Success -> destination.add(result.value)
        }.exhaustive()
    }
    return Success(destination)
}

@Suppress("NOTHING_TO_INLINE")
inline fun Any?.exhaustive() = Unit