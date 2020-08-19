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

package com.dev.musicapp.models

import android.database.Cursor
import com.dev.musicapp.utils.BeatConstants.ALBUM_TYPE
import com.google.gson.Gson


data class Album(
    var id: Long = 0,
    var title: String = "",
    var artist: String = "",
    var artistId: Long = 0,
    var songCount: Int = 0,
    var year: Int = 0
) : MediaItem(id) {

    companion object {
        fun createFromCursor(cursor: Cursor, artistId: Long = 0): Album {
            return Album(
                cursor.getLong(0),
                cursor.getString(1),
                cursor.getString(2),
                if (artistId == 0L) cursor.getLong(3) else artistId,
                if (artistId == 0L) cursor.getInt(4) else cursor.getInt(3),
                if (artistId == 0L) cursor.getInt(5) else cursor.getInt(4)
            )
        }
    }

    override fun toString(): String {
        return Gson().toJson(this)
    }

    fun toFavorite(): Favorite {
        return Favorite(id, title, artist, artistId, year, songCount, ALBUM_TYPE)
    }
}