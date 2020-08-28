

package com.dev.musicapp.models

import android.database.Cursor
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import com.dev.musicapp.extensions.fix
import com.dev.musicapp.repository.FavoritesRepositoryImplementation.Companion.COLUMN_FAVORITE
import com.dev.musicapp.repository.PlaylistRepositoryImplementation.Companion.COLUMN_ALBUM
import com.dev.musicapp.repository.PlaylistRepositoryImplementation.Companion.COLUMN_ALBUM_ID
import com.dev.musicapp.repository.PlaylistRepositoryImplementation.Companion.COLUMN_ARTIST
import com.dev.musicapp.repository.PlaylistRepositoryImplementation.Companion.COLUMN_ARTIST_ID
import com.dev.musicapp.repository.PlaylistRepositoryImplementation.Companion.COLUMN_DURATION
import com.dev.musicapp.repository.PlaylistRepositoryImplementation.Companion.COLUMN_ID
import com.dev.musicapp.repository.PlaylistRepositoryImplementation.Companion.COLUMN_PLAYLIST
import com.dev.musicapp.repository.PlaylistRepositoryImplementation.Companion.COLUMN_TITLE
import com.dev.musicapp.repository.PlaylistRepositoryImplementation.Companion.COLUMN_TRACK
import com.dev.musicapp.utils.BeatConstants.FAVORITE_TYPE
import com.dev.musicapp.utils.BeatConstants.SONG_TYPE
import com.dev.musicapp.utils.GeneralUtils.getAlbumArtUri
import com.dev.musicapp.utils.GeneralUtils.getSongUri
import java.io.File

data class Song(
    val id: Long = -1,
    val albumId: Long = 0,
    val artistId: Long = 0,
    val title: String = "Title",
    val artist: String = "Artist",
    val album: String = "Album",
    val duration: Int = 0,
    val trackNumber: Int = 0,
    val path: String = "",
    var isFav: Boolean = false,
    var isSelected: Boolean = false,
    var playListId: Long = -1
) : MediaItem(id) {
    companion object {
        fun createFromCursor(cursor: Cursor, album_id: Long = 0): Song {
            return Song(
                id = cursor.getLong(0),
                title = cursor.getString(1),
                artist = cursor.getString(2),
                album = cursor.getString(3),
                duration = cursor.getInt(4),
                trackNumber = cursor.getInt(5).fix(),
                artistId = cursor.getLong(6),
                albumId = if (album_id == 0L) cursor.getLong(7) else album_id,
                path = getSongUri(cursor.getLong(0)).toString()
            )
        }

        fun createFromPlaylistCursor(cursor: Cursor): Song {
            return Song(
                id = cursor.getLong(0),
                title = cursor.getString(1),
                artist = cursor.getString(2),
                album = cursor.getString(3),
                duration = cursor.getInt(4),
                trackNumber = cursor.getInt(5),
                artistId = cursor.getLong(6),
                albumId = cursor.getLong(7),
                playListId = cursor.getLong(8),
                path = getSongUri(cursor.getLong(0)).toString()
            )
        }

        fun createFromFolderCursor(cursor: Cursor): Song {
            return Song(
                id = cursor.getLong(0),
                title = cursor.getString(1),
                artist = cursor.getString(2),
                album = cursor.getString(3),
                duration = cursor.getInt(4),
                trackNumber = cursor.getInt(5),
                artistId = cursor.getLong(6),
                albumId = cursor.getLong(7),
                path = File(cursor.getString(8)).parent!!
            )
        }
    }

    override fun compare(other: MediaItem): Boolean {
        other as Song
        return id == other.id && title == other.title && artist == other.artist && album == other.album
                && duration == other.duration && trackNumber == other.trackNumber && artistId == other.artistId
                && albumId == other.albumId && path == other.path
    }


    fun columns(type: String): Array<String> {
        return arrayOf(
            COLUMN_ID,
            COLUMN_TITLE,
            COLUMN_ARTIST,
            COLUMN_ALBUM,
            COLUMN_DURATION,
            COLUMN_TRACK,
            COLUMN_ARTIST_ID,
            COLUMN_ALBUM_ID,
            when (type) {
                FAVORITE_TYPE -> COLUMN_FAVORITE
                else -> COLUMN_PLAYLIST
            }
        )
    }

    fun values(): Array<String> {
        return arrayOf(
            "$id",
            title,
            artist,
            album,
            "$duration",
            "$trackNumber",
            "$artistId",
            "$albumId",
            "$playListId"
        )
    }

    fun toMediaItem(): MediaBrowserCompat.MediaItem {
        return MediaBrowserCompat.MediaItem(
            MediaDescriptionCompat.Builder()
                .setMediaId(MediaId(SONG_TYPE, id.toString(), null).toString())
                .setTitle(title)
                .setIconUri(getAlbumArtUri(albumId))
                .setSubtitle(artist)
                .build(), MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
        )
    }
}
