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

import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_MEDIA_ID
import android.support.v4.media.session.MediaControllerCompat
import com.dev.musicapp.extensions.toIdList

data class Queue(
    var queueTitle: String = "",
    var queue: LongArray = LongArray(0),
    val currentId: Long = 0
) {

    companion object {
        fun fromMediaController(mediaController: MediaControllerCompat): Queue? {
            return Queue(
                queueTitle = mediaController.queueTitle?.toString().orEmpty(),
                queue = mediaController.queue.toIdList(),
                currentId = mediaController.metadata?.getString(METADATA_KEY_MEDIA_ID)?.toLong()
                    ?: 0
            )
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Queue

        if (queueTitle != other.queueTitle) return false
        if (!queue.contentEquals(other.queue)) return false
        if (currentId != other.currentId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = queueTitle.hashCode()
        result = 31 * result + queue.contentHashCode()
        result = 31 * result + currentId.hashCode()
        return result
    }

    fun clear() {
        queueTitle = ""
        queue = longArrayOf()
    }
}