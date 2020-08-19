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

import android.support.v4.media.session.PlaybackStateCompat
import com.dev.musicapp.utils.BeatConstants

class PlaybackState(
    val position: Int = 0,
    val shuffleMode: Int = 0,
    val repeatMode: Int = 0,
    val state: Int = 0
) {

    companion object {
        fun pullPlaybackState(playbackState: PlaybackStateCompat): PlaybackState {
            with(playbackState.extras) {
                return PlaybackState(
                    position = playbackState.position.toInt(),
                    state = playbackState.state,
                    repeatMode = this?.getInt(BeatConstants.REPEAT_MODE)
                        ?: PlaybackStateCompat.REPEAT_MODE_ONE,
                    shuffleMode = this?.getInt(BeatConstants.SHUFFLE_MODE)
                        ?: PlaybackStateCompat.REPEAT_MODE_ALL
                )
            }
        }
    }
}