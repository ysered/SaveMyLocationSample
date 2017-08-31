package com.ysered.savemylocationsample.util

import android.app.Activity
import android.widget.Toast

fun Activity.showToast(text: String, duration: Int = android.widget.Toast.LENGTH_SHORT) {
    Toast.makeText(this, text, duration).show()
}

fun Activity.showToast(textResId: Int, duration: Int = android.widget.Toast.LENGTH_SHORT) {
    Toast.makeText(this, textResId, duration).show()
}
