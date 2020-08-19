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

import android.support.v4.media.MediaBrowserCompat

class MediaId(
    val type: String? = "0",
    val mediaId: String? = "0",
    val caller: String? = ""
) {

    companion object {
        const val CALLER_SELF = "self"
        const val CALLER_OTHER = "other"
    }

    var mediaItem: MediaBrowserCompat.MediaItem? = null

    override fun toString(): String {
        return "$type | $mediaId | $caller"
    }
}