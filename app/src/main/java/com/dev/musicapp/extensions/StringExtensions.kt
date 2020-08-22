
package com.dev.musicapp.extensions

import com.dev.musicapp.models.MediaId
import com.dev.musicapp.models.QueueInfo
import com.dev.musicapp.models.Song
import com.google.gson.Gson

fun String.toSong(): Song {
    return Gson().fromJson(this)
}

fun String.toQueueInfo(): QueueInfo {
    return Gson().fromJson(this)
}

fun String.toQueueList(): LongArray {
    return Gson().fromJson(this)
}

fun String.toMediaId(): MediaId {
    val parts = split("|")
    return if (parts.size > 1)
        MediaId(parts[0].trim(), parts[1].trim(), parts[2].trim())
    else MediaId()
}

fun String.fixName(): String {
    val index = indexOf("(")
    val nameFixed = if (index != -1) {
        substring(0, index)
    } else this
    return nameFixed.trim()
}