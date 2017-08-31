package com.ysered.savemylocationsample.util

import android.util.Log
import com.ysered.savemylocationsample.BuildConfig


fun Any.info(text: String, t: Throwable? = null) {
    if (BuildConfig.DEBUG) {
        Log.i(this::class.java.canonicalName, text, t)
    }
}

fun Any.debug(text: String, t: Throwable? = null) {
    if (BuildConfig.DEBUG) {
        Log.d(this::class.java.canonicalName, text, t)
    }
}

fun Any.error(text: String, t: Throwable? = null) {
    if (BuildConfig.DEBUG) {
        Log.e(this::class.java.canonicalName, text, t)
    }
}
