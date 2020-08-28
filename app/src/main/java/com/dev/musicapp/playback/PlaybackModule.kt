

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