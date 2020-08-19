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

package com.dev.musicapp.playback
import com.dev.musicapp.playback.players.BeatMediaPlayer
import com.dev.musicapp.playback.players.BeatMediaPlayerImplementation
import com.dev.musicapp.playback.players.BeatPlayer
import com.dev.musicapp.playback.players.BeatPlayerImplementation
import org.koin.dsl.bind
import org.koin.dsl.module

val playbackModule = module {
    factory {
        BeatMediaPlayerImplementation(get())
    } bind (BeatMediaPlayer::class)

    factory {
        AudioFocusHelperImplementation(get())
    } bind (AudioFocusHelper::class)

    factory {
        BeatPlayerImplementation(
            get(),
            get(),
            get(),
            get(),
            get(),
            get()
        )
    } bind (BeatPlayer::class)
}