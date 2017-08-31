package com.ysered.savemylocationsample.util

import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


private inline fun <T> SharedPreferences.delegate(
        defaultValue: T,
        key: String?,
        crossinline getter: SharedPreferences.(String, T) -> T,
        crossinline setter: Editor.(String, T) -> Editor
): ReadWriteProperty<Any, T> {
    return object : ReadWriteProperty<Any, T> {
        override fun getValue(thisRef: Any, property: KProperty<*>)
                = getter(key ?: property.name, defaultValue)

        override fun setValue(thisRef: Any, property: KProperty<*>, value: T)
                = edit().setter(key ?: property.name, value).apply()
    }
}

fun SharedPreferences.int(defaultValue: Int = 0, key: String? = null)
        = delegate(defaultValue, key, SharedPreferences::getInt, Editor::putInt)

fun SharedPreferences.float(defaultValue: Float = 0f, key: String? = null)
        = delegate(defaultValue, key, SharedPreferences::getFloat, Editor::putFloat)
