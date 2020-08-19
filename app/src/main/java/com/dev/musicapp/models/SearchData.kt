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

import com.dev.musicapp.utils.BeatConstants.ALBUM_TYPE
import com.dev.musicapp.utils.BeatConstants.ARTIST_TYPE

data class SearchData(
    var songList: MutableList<Song> = mutableListOf(),
    var albumList: MutableList<Album> = mutableListOf(),
    var artistList: MutableList<Artist> = mutableListOf()
) {

    fun isNotEmpty(): Boolean {
        return isNotSongListEmpty() || isNotAlbumListEmpty() || isNotArtistListEmpty()
    }

    fun isNotEmpty(type: String): Boolean {
        return when (type) {
            ALBUM_TYPE -> isNotAlbumListEmpty()
            ARTIST_TYPE -> isNotArtistListEmpty()
            else -> isNotSongListEmpty()
        }
    }

    fun isNotSongListEmpty(): Boolean {
        return songList.isNotEmpty()
    }

    fun isNotAlbumListEmpty(): Boolean {
        return albumList.isNotEmpty()
    }

    fun isNotArtistListEmpty(): Boolean {
        return artistList.isNotEmpty()
    }

    fun flush(): SearchData {
        return SearchData()
    }
}