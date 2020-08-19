package com.dev.musicapp.utils

import com.dev.musicapp.models.Artist
import java.util.*

object SortModes {
    class SongModes {
        companion object {
            const val SONG_A_Z = "title"
            const val SONG_Z_A = "$SONG_A_Z DESC"
            const val SONG_DURATION = "duration DESC"
            const val SONG_YEAR = "year"
            const val SONG_LAST_ADDED = "date_added DESC"
            const val SONG_ALBUM = "album"
            const val SONG_ARTIST = "artist"
            const val SONG_TRACK = "track, title_key"
        }
    }

    class AlbumModes {
        companion object {
            const val ALBUM_A_Z = "album"
            const val ALBUM_Z_A = "$ALBUM_A_Z DESC"
            const val ALBUM_YEAR = "minyear DESC"
            const val ALBUM_SONG_COUNT = "numsongs"
            const val ALBUM_ARTIST = "artist"
        }
    }

    class ArtistModes {
        companion object {
            const val ARTIST_A_Z = "artist"
            const val ARTIST_Z_A = "$ARTIST_A_Z DESC"
            const val ARTIST_ALBUM_COUNT = "number_of_albums"
            const val ARTIST_SONG_COUNT = "numsongs"

            fun sortArtistList(artistList: MutableList<Artist>, sortMode: String) {
                when (sortMode) {
                    ARTIST_A_Z -> artistList.sortBy { it.name.toLowerCase(Locale.ROOT) }
                    ARTIST_Z_A -> artistList.sortByDescending { it.name.toLowerCase(Locale.ROOT) }
                    ARTIST_SONG_COUNT -> artistList.sortBy { it.songCount }
                    ARTIST_ALBUM_COUNT -> artistList.sortBy { it.albumCount }
                }
            }
        }
    }


}
