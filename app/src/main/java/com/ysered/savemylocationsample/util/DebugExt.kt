package com.ysered.savemylocationsample.util

import com.ysered.savemylocationsample.BuildConfig


fun Any.debug(text: String, t: Throwable? = null) {
    if (BuildConfig.DEBUG) {
        android.util.Log.d(this::class.java.canonicalName, text, t)
    }
}

fun Any.error(text: String, t: Throwable? = null) {
    if (BuildConfig.DEBUG) {
        android.util.Log.d(this::class.java.canonicalName, text, t)
    }
}
