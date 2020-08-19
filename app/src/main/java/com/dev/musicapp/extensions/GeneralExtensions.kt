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

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.ParcelFileDescriptor
import com.dev.musicapp.utils.BeatConstants.READ_ONLY_MODE
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.FileNotFoundException

inline fun <reified T> Gson.fromJson(json: String): T =
    this.fromJson<T>(json, object : TypeToken<T>() {}.type)

fun Uri.toFileDescriptor(context: Context): ParcelFileDescriptor? {
    return try {
        context.contentResolver.openFileDescriptor(this, READ_ONLY_MODE, null)
    } catch (ex: FileNotFoundException) {
        null
    }
}

fun ByteArray.optimize(): ByteArray {
    return copyOfRange(44, (size / 2) - 1)
}

operator fun Bundle.plus(other: Bundle) = this.apply { putAll(other) }