package com.dev.musicapp.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dev.musicapp.ui.viewmodels.base.CoroutineViewModel
import com.dev.musicapp.models.Song
import com.dev.musicapp.repository.SongsRepository
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.withContext

class SongViewModel(
    private val songsRepository: SongsRepository
) : CoroutineViewModel(Main) {

    private val songsData = MutableLiveData<List<Song>>().apply { value = mutableListOf() }
    private val songsSelected = MutableLiveData<MutableList<Song>>().apply { value = mutableListOf() }

    fun update() {
        launch {
            val songs = withContext(IO) {
                songsRepository.loadSongs()
            }
            if (songs.isNotEmpty()) songsData.postValue(songs)
        }
    }

    fun update(song: MutableList<Song>) {
        songsSelected.postValue(song)
    }

    fun getSongList(): LiveData<List<Song>> {
        update()
        return songsData
    }

    fun selectedSongs(): LiveData<MutableList<Song>> {
        return songsSelected
    }

    fun delete(ids: LongArray): Int {
        return songsRepository.deleteTracks(ids)
    }

    fun getSongById(id: Long): Song {
        return songsRepository.getSongForId(id)
    }

    fun getSongFromPath(path: String): Song {
        return songsRepository.getSongForPath(path)
    }
}