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

import android.support.v4.media.MediaMetadataCompat

class MediaItemData(
    val id: Long = 0L,
    val title: String = "",
    val album: String = "",
    val artist: String = "",
    val albumId: Long = 0,
    val duration: Int = 0,
    val description: String = ""

) {
    companion object {
        fun pullMediaMetadata(metaData: MediaMetadataCompat?): MediaItemData? {
            metaData ?: return null
            val mediaId = metaData.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)
            val data = MediaItemData(
                id = if (mediaId == "") -1 else mediaId.toLong(),
                title = metaData.getString(MediaMetadataCompat.METADATA_KEY_TITLE) ?: "",
                album = metaData.getString(MediaMetadataCompat.METADATA_KEY_ALBUM) ?: "",
                artist = metaData.getString(MediaMetadataCompat.METADATA_KEY_ARTIST) ?: "",
                duration = metaData.getLong(MediaMetadataCompat.METADATA_KEY_DURATION).toInt(),
                albumId = metaData.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI)
                    ?.toLong() ?: 0,
                description = metaData.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION)
                    ?: ""
            )
            return if (data.id != -1L) data else null
        }
    }
}