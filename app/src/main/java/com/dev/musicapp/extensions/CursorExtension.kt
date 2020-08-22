

package com.dev.musicapp.extensions

import android.database.Cursor

fun Cursor?.forEach(
    closeAfter: Boolean = false,
    each: Cursor.() -> Unit
) {
    this ?: return
    if (moveToFirst()) {
        do {
            each(this)
        } while (moveToNext())
    }
    if (closeAfter) {
        close()
    }
}

fun <T> Cursor?.toList(
    close: Boolean = false,
    mapper: Cursor.() -> T
): MutableList<T> {
    val result = mutableListOf<T>()
    forEach(close) {
        result.add(mapper(this))
    }
    return result
}