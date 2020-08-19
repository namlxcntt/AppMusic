package com.dev.musicapp.utils

import com.crashlytics.android.Crashlytics
import org.jetbrains.annotations.NotNull
import timber.log.Timber

class ReleaseTree : @NotNull Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        try {
            if (t != null) {
                Crashlytics.setString("beatplayer_crash_tag", tag)
                Crashlytics.logException(t)
            } else {
                Crashlytics.log(priority, tag, message)
            }
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }
}