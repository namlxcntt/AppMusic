package com.dev.musicapp

import android.app.Application
import com.dev.musicapp.ui.viewmodels.viewModelModule
import com.dev.musicapp.notifications.notificationModule
import com.dev.musicapp.playback.playbackModule
import com.dev.musicapp.repository.repositoriesModule
import com.dev.musicapp.utils.ReleaseTree
import com.dev.musicapp.utils.utilsModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber


class MusicApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        //Fabric.with(this, Crashlytics())

        if (true) { // Ti nua them vao build config
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(ReleaseTree())
        }

        val modules = listOf(
            mainModel,
            notificationModule,
            playbackModule,
            repositoriesModule,
            viewModelModule,
            utilsModule
        )
        startKoin {
            androidContext(this@MusicApplication)
            modules(modules)
        }
    }
}