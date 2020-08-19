/*
 * Copyright (c) 2020. Carlos René Ramos López. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dev.musicapp.repository

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import com.dev.musicapp.extensions.toList
import com.dev.musicapp.models.Album
import com.dev.musicapp.models.Song
import com.dev.musicapp.utils.BeatConstants
import com.dev.musicapp.utils.SettingsUtility

interface AlbumsRepository {
    fun getAlbum(id: Long): Album
    fun getSongsForAlbum(albumId: Long): List<Song>
    fun getAlbums(): List<Album>
    fun search(paramString: String, limit: Int = Int.MAX_VALUE): List<Album>
}

class AlbumsRepositoryImplementation(context: Context) : AlbumsRepository {

    private val contentResolver = context.contentResolver
    private val settingsUtility = SettingsUtility(context)

    private fun getAlbum(cursor: Cursor?): Album {
        return cursor?.use {
            if (cursor.moveToFirst()) {
                Album.createFromCursor(cursor)
            } else {
                null
            }
        } ?: Album()
    }

    override fun getAlbum(id: Long): Album {
        return getAlbum(makeAlbumCursor("_id=?", arrayOf(id.toString())))
    }

    override fun getSongsForAlbum(albumId: Long): List<Song> {
        return makeAlbumSongCursor(albumId)
            .toList(true) { Song.createFromCursor(this, albumId) }
    }

    override fun getAlbums(): List<Album> {
        return makeAlbumCursor(null, null)
            .toList(true) { Album.createFromCursor(this) }
    }

    override fun search(paramString: String, limit: Int): List<Album> {
        val result = makeAlbumCursor("album LIKE ?", arrayOf("$paramString%"))
            .toList(true) { Album.createFromCursor(this) }
        if (result.size < limit) {
            val moreResults = makeAlbumCursor("album LIKE ?", arrayOf("%_$paramString%"))
                .toList(true) { Album.createFromCursor(this) }
            result += moreResults
        }
        return if (result.size < limit) {
            result
        } else {
            result.subList(0, limit)
        }
    }

    private fun makeAlbumCursor(selection: String?, paramArrayOfString: Array<String>?): Cursor? {
        return contentResolver.query(
            MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
            arrayOf("_id", "album", "artist", "artist_id", "numsongs", "minyear"),
            selection,
            paramArrayOfString,
            settingsUtility.albumSortOrder
        )
    }

    private fun makeAlbumSongCursor(albumId: Long): Cursor? {
        val selection = "is_music = ? AND title != ? AND album_id = ?"
        return contentResolver.query(
            BeatConstants.SONG_URI,
            arrayOf("_id", "title", "artist", "album", "duration", "track", "artist_id", "_data"),
            selection,
            arrayOf("1", "''", "$albumId"),
            settingsUtility.albumSongSortOrder
        )
    }
}