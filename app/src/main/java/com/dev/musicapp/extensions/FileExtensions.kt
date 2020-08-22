
package com.dev.musicapp.extensions

import android.content.Context
import com.dev.musicapp.utils.GeneralUtils.getStoragePaths
import timber.log.Timber
import java.io.File

private const val INTERNAL_STORAGE = "/Internal Storage"
private const val EXTERNAL_STORAGE = "/SD Card"
private const val MEGA_BYTES_SIZE = 1048576

fun File.fixedPath(context: Context): String {
    val storagePaths = getStoragePaths(context)
    val type = path.contains(storagePaths[0])
    Timber.d("fixedPath()")
    Timber.d("path: $storagePaths")
    return when{
        storagePaths.isNotEmpty() && type -> path.replace(storagePaths[0], INTERNAL_STORAGE)
        storagePaths.size > 1 && !type -> path.replace(storagePaths[1], EXTERNAL_STORAGE)
        else -> path
    }
}

fun File.fixedName(context: Context): String {
    return File(fixedPath(context)).name
}

fun File.sizeMB(): String? {
    return "${length() / MEGA_BYTES_SIZE} MB"
}