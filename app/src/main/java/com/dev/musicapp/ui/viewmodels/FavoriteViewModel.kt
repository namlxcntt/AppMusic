package com.dev.musicapp.ui.viewmodels


import android.database.sqlite.SQLiteException
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dev.musicapp.ui.viewmodels.base.CoroutineViewModel
import com.dev.musicapp.models.Favorite
import com.dev.musicapp.models.Song
import com.dev.musicapp.repository.FavoritesRepository
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.withContext

class FavoriteViewModel(
    private val repository: FavoritesRepository
) : CoroutineViewModel(Main) {

    private val favoriteListData = MutableLiveData<List<Favorite>>()
    private val songListData = MutableLiveData<List<Song>>()

    fun deleteFavorite(id: Long) {
        repository.deleteFavorites(longArrayOf(id))
    }

    fun deleteSongByFavorite(favoriteId: Long, ids: LongArray): Int {
        return repository.deleteSongByFavorite(favoriteId, ids)
    }

    fun songListFavorite(idFavorites: Long): LiveData<List<Song>> {
        launch {
            val songs = withContext(IO) {
                repository.getSongsForFavorite(idFavorites)
            }
            songListData.postValue(songs)
        }
        return songListData
    }

    fun getFavorites(): LiveData<List<Favorite>> {
        launch {
            val favorites = withContext(IO) {
                try {
                    repository.getFavorites()
                } catch (ex: SQLiteException) {
                    emptyList<Favorite>()
                } catch (ex: IllegalStateException) {
                    emptyList<Favorite>()
                }
            }
            favoriteListData.postValue(favorites)
        }
        return favoriteListData
    }

    fun getFavorite(id: Long): Favorite {
        return repository.getFavorite(id)
    }

    fun deleteFavorites(ids: LongArray): Int {
        return repository.deleteFavorites(ids)
    }

    fun addToFavorite(favoriteId: Long, songs: List<Song>): Int {
        return repository.addSongByFavorite(favoriteId, songs)
    }

    fun remove(favoriteId: Long, ids: LongArray) {
        repository.deleteSongByFavorite(favoriteId, ids)
    }

    fun favExist(favoriteId: Long): Boolean {
        return repository.favExist(favoriteId)
    }

    fun songExist(songId: Long): Boolean {
        return repository.songExist(songId)
    }

    fun create(favorite: Favorite): Int {
        return repository.createFavorite(favorite)
    }

    fun update(parentId: Long, id: Long): Int {
        return repository.updateFavoriteCount(parentId, id)
    }
}