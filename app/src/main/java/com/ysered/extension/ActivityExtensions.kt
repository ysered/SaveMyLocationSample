package com.ysered.extension

import android.widget.Toast
import com.ysered.savemylocationsample.BuildConfig

fun Any.debug(text: String, t: Throwable? = null) {
    if (BuildConfig.DEBUG) {
        android.util.Log.d(this::class.java.canonicalName, text, t)
    }
}

fun android.app.Activity.showToast(text: String, duration: Int = android.widget.Toast.LENGTH_SHORT) {
    Toast.makeText(this, text, duration).show()
}

fun android.app.Activity.showToast(textResId: Int, duration: Int = android.widget.Toast.LENGTH_SHORT) {
    Toast.makeText(this, textResId, duration).show()
}
