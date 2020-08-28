

package com.dev.musicapp.models

import android.database.Cursor
import com.dev.musicapp.repository.PlaylistRepositoryImplementation.Companion.COLUMN_COVER_ID
import com.dev.musicapp.repository.PlaylistRepositoryImplementation.Companion.COLUMN_ID
import com.dev.musicapp.repository.PlaylistRepositoryImplementation.Companion.COLUMN_NAME
import com.dev.musicapp.repository.PlaylistRepositoryImplementation.Companion.COLUMN_SONG_COUNT
import com.google.gson.Gson

data class Playlist(
    val id: Long = -1,
    val name: String = "",
    val albumId: Long = -1,
    val songCount: Int = -1
) : MediaItem(id) {
    companion object {
        fun fromCursor(cursor: Cursor): Playlist {
            return Playlist(
                cursor.getLong(0),
                cursor.getString(1),
                cursor.getLong(2),
                cursor.getInt(3)
            )
        }
    }

    override fun compare(other: MediaItem): Boolean {
        other as Playlist
        return id == other.id && name == other.name && songCount == other.songCount
    }

    override fun toString(): String {
        return Gson().toJson(this)
    }

    fun columns(): Array<String> {
        return arrayOf(COLUMN_ID, COLUMN_NAME, COLUMN_COVER_ID, COLUMN_SONG_COUNT)
    }

    fun values(): Array<String> {
        return arrayOf("$id", name, "-1", "$songCount")
    }
}