package com.dev.musicapp.ui.viewmodels


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dev.musicapp.ui.viewmodels.base.CoroutineViewModel
import com.dev.musicapp.models.Folder
import com.dev.musicapp.models.Song
import com.dev.musicapp.repository.FoldersRepository
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class FolderViewModel(
    private val repository: FoldersRepository
) : CoroutineViewModel(Main) {

    private val foldersData: MutableLiveData<List<Folder>> = MutableLiveData()
    private val songByFolder: MutableLiveData<List<Song>> = MutableLiveData()


    fun getFolders(): LiveData<List<Folder>> {
        launch {
            val folders = withContext(IO) {
                repository.getFolders()
            }
            foldersData.postValue(folders)
        }
        return foldersData
    }

    fun getSongsByFolder(ids: LongArray): LiveData<List<Song>> {
        launch {
            while (true) {
                val songs = withContext(IO) {
                    repository.getSongs(ids)
                }
                songByFolder.postValue(songs)
                delay(1000)
            }
        }
        return songByFolder
    }

    fun getFolder(id: Long): Folder {
        return repository.getFolder(id)
    }
}
