

package com.dev.musicapp.utils

import com.dev.musicapp.utils.QueueUtils
import com.dev.musicapp.utils.QueueUtilsImplementation
import com.dev.musicapp.utils.SettingsUtility
import org.koin.dsl.bind
import org.koin.dsl.module

val utilsModule = module {
    factory { SettingsUtility(get()) }
    factory { QueueUtilsImplementation(get(), get()) } bind QueueUtils::class
}