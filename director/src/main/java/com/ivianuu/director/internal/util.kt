package com.ivianuu.director.internal

import android.os.Looper

fun <T : Any> newInstanceOrThrow(className: String) = try {
    classForNameOrThrow<T>(className).newInstance() as T
} catch (e: Exception) {
    throw RuntimeException("could not instantiate $className, $e")
}

fun <T> classForNameOrThrow(className: String) = try {
    Class.forName(className) as Class<out T>
} catch (e: Exception) {
    throw RuntimeException("couldn't find class $className")
}

internal fun requireMainThread() {
    if (Looper.getMainLooper() != Looper.myLooper()) {
        throw IllegalStateException("must be called from the main thread")
    }
}