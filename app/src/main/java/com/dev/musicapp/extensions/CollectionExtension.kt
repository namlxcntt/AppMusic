
package com.dev.musicapp.extensions

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaSessionCompat
import com.dev.musicapp.models.MediaItem
import com.dev.musicapp.models.Song
import com.dev.musicapp.repository.SongsRepository

fun <T> List<T>?.moveElement(fromIndex: Int, toIndex: Int): List<T> {
    if (this == null) {
        return emptyList()
    }
    return toMutableList().apply {
        val deleted = removeAt(fromIndex)
        add(toIndex, deleted)
    }
}

fun <E> MutableList<E>.setAll(list: List<E>) {
    clear()
    addAll(list)
}

fun <T> MutableList<T>.delete(item: T) {
    item ?: throw NullPointerException("The index can't be null.")

    setAll(filterNot { it == item }.optimizeReadOnlyList())
}

internal fun <T> List<T>.optimizeReadOnlyList() = when (size) {
    0 -> emptyList()
    1 -> listOf(this[0])
    else -> this
}

fun <T : MediaItem> Collection<T>.deepEquals(
    list2: Collection<T>
) = when {
    size != list2.size || size == 0 -> false
    else -> zip(list2).all { (elt1, elt2) -> elt1.compare(elt2) }
}

fun List<MediaItem>?.toIDList(): LongArray {
    return this?.map { it._id }?.toLongArray() ?: LongArray(0)
}

fun List<MediaSessionCompat.QueueItem>?.toIdList(): LongArray {
    return this?.map { it.queueId }?.toLongArray() ?: LongArray(0)
}

fun List<Song?>.toQueue(): List<MediaSessionCompat.QueueItem> {
    return filterNotNull().map {
        MediaSessionCompat.QueueItem(it.toDescription(), it.id)
    }
}

fun List<Song>.toMediaItemList(): List<MediaBrowserCompat.MediaItem> {
    return this.map { it.toMediaItem() }
}

fun LongArray.toQueue(songsRepository: SongsRepository): List<MediaSessionCompat.QueueItem> {
    val songList = songsRepository.getSongsForIds(this)
    songList.keepInOrder(this)?.let {
        return it.toQueue()
    } ?: return songList.toQueue()
}

fun List<Song>.keepInOrder(queue: LongArray): List<Song>? {
    if (size != queue.size) return this
    return if (isNotEmpty() && queue.isNotEmpty()) {
        val keepOrderList = Array(size, init = { Song() })
        forEach {
            keepOrderList[queue.indexOf(it.id)] = it
        }
        keepOrderList.asList()
    } else null
}