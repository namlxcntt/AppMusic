
package com.dev.musicapp.extensions

import android.content.Context
import android.util.TypedValue
import androidx.annotation.AttrRes

@Suppress("UNCHECKED_CAST")
fun <T> Context.systemService(name: String): T {
    return getSystemService(name) as T
}

fun Context.getColorByTheme(
    @AttrRes id: Int
): Int {
    val colorAttr: Int? = id
    val outValue = TypedValue()
    theme.resolveAttribute(colorAttr!!, outValue, true)
    return outValue.data
}