package com.dev.musicapp

import android.content.ComponentName
import com.dev.musicapp.playback.PlaybackConnection
import com.dev.musicapp.playback.PlaybackConnectionImplementation
import com.dev.musicapp.playback.services.BeatPlayerService
import org.koin.dsl.bind
import org.koin.dsl.module

val mainModel = module {
    single {
        val component = ComponentName(get(), BeatPlayerService::class.java)
        PlaybackConnectionImplementation(get(), component)
    } bind PlaybackConnection::class
}