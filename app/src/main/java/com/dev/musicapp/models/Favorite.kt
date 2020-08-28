

package com.dev.musicapp.models

import android.database.Cursor
import com.dev.musicapp.repository.FavoritesRepositoryImplementation.Companion.COLUMN_ARTIST
import com.dev.musicapp.repository.FavoritesRepositoryImplementation.Companion.COLUMN_ARTIST_ID
import com.dev.musicapp.repository.FavoritesRepositoryImplementation.Companion.COLUMN_ID
import com.dev.musicapp.repository.FavoritesRepositoryImplementation.Companion.COLUMN_SONG_COUNT
import com.dev.musicapp.repository.FavoritesRepositoryImplementation.Companion.COLUMN_TITLE
import com.dev.musicapp.repository.FavoritesRepositoryImplementation.Companion.COLUMN_TYPE
import com.dev.musicapp.repository.FavoritesRepositoryImplementation.Companion.COLUMN_YEAR
import com.dev.musicapp.utils.BeatConstants.ARTIST_TYPE
import com.dev.musicapp.utils.BeatConstants.FAVORITE_TYPE
import com.dev.musicapp.utils.BeatConstants.FOLDER_TYPE

class Favorite(
    var id: Long = -1,
    var title: String = "",
    var artist: String = "",
    var artistId: Long = -1,
    var year: Int = -1,
    var songCount: Int = -1,
    val type: String = ""
) : MediaItem(id) {
    companion object {
        fun fromCursor(cursor: Cursor): Favorite {
            return Favorite(
                cursor.getLong(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getLong(3),
                cursor.getInt(4),
                cursor.getInt(5),
                cursor.getString(6)
            )
        }
    }

    fun columnNames(): Array<String> {
        return arrayOf(
            COLUMN_ID,
            COLUMN_TITLE,
            COLUMN_ARTIST,
            COLUMN_ARTIST_ID,
            COLUMN_SONG_COUNT,
            COLUMN_YEAR,
            COLUMN_TYPE
        )
    }

    fun values(): Array<String> {
        return arrayOf("$id", title, artist, "$artistId", "$songCount", "$year", type)
    }

    fun getIdByType(): Long {
        return if (type in listOf(ARTIST_TYPE, FOLDER_TYPE, FAVORITE_TYPE)) artistId else id
    }

    override fun compare(other: MediaItem): Boolean {
        other as Favorite
        return id == other.id && title == other.title && artist == other.artist &&
                artistId == other.artistId && year == other.year && songCount == other.songCount &&
                type == other.type
    }
}
