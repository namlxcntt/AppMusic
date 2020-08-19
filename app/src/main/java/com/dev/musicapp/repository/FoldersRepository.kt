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

package com.dev.musicapp.repository

import android.content.Context
import com.dev.musicapp.extensions.toIDList
import com.dev.musicapp.models.Folder
import com.dev.musicapp.models.Song

interface FoldersRepository {
    fun getFolders(): List<Folder>
    fun getFolder(id: Long): Folder
    fun getSongs(ids: LongArray): List<Song>
}

class FoldersRepositoryImplementation(
    private val context: Context,
    private val songsRepository: SongsRepository
) : FoldersRepository {

    override fun getFolders(): List<Folder> {
        return songsRepository.getSongs().groupBy { it.path }.map {
            Folder.fromSong(it.value.first(), it.value.toIDList(), context)
        }.sortedBy { it.name }
    }

    override fun getFolder(id: Long): Folder {
        return songsRepository.getSongs().groupBy { it.path }.filter {
            it.value.first().id == id
        }.map {
            Folder.fromSong(it.value.first(), it.value.toIDList(), context)
        }.firstOrNull() ?: Folder()
    }

    override fun getSongs(ids: LongArray): List<Song> {
        return songsRepository.getSongsForIds(ids)
    }
}