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

package com.dev.musicapp.extensions

import android.support.v4.media.MediaDescriptionCompat
import com.dev.musicapp.models.Song
import com.dev.musicapp.utils.GeneralUtils
import com.dev.musicapp.utils.GeneralUtils.getAlbumArtUri

fun Int.format(): String {
    return GeneralUtils.formatMilliseconds(this.toLong())
}

fun Int.fix(): Int {
    var value = this
    while (value >= 1000) {
        value -= 1000
    }
    return value
}

fun Float.fixPercentBounds(): Float {
    return if (this < 0F) 0F else if (this > 100F) 100F else this
}

fun Int.fixToStep(step: Int): Int {
    return this / step * step
}

fun Int.fixToPercent(total: Int): Float {
    return (this * 100) / total.toFloat()
}

fun Float.percentToMs(total: Int): Int {
    return (this * total / 100).toInt()
}

fun Song.toDescription(): MediaDescriptionCompat {
    return MediaDescriptionCompat.Builder()
        .setTitle(title)
        .setMediaId(id.toString())
        .setSubtitle(artist)
        .setDescription(album)
        .setIconUri(getAlbumArtUri(albumId)).build()
}