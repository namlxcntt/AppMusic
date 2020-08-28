
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