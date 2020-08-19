package com.dev.musicapp.ui.viewmodels

import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.view.animation.AccelerateInterpolator
import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dev.musicapp.databinding.ActivityMainBinding
import com.dev.musicapp.ui.viewmodels.base.CoroutineViewModel
import com.dev.musicapp.playback.PlaybackConnection
import com.dev.musicapp.repository.FavoritesRepository
import com.dev.musicapp.utils.BeatConstants.QUEUE_LIST_TYPE_KEY
import com.dev.musicapp.utils.BeatConstants.UPDATE_QUEUE
import com.dev.musicapp.utils.SettingsUtility.Companion.QUEUE_LIST_KEY
import com.github.florent37.kotlin.pleaseanimate.please
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.withContext

class MainViewModel(
    private val favoritesRepository: FavoritesRepository,
    private val PlaybackConnection: PlaybackConnection
) : CoroutineViewModel(Main) {

    private val isFavLiveData = MutableLiveData<Boolean>()
    private val isAlbumFavLiveData = MutableLiveData<Boolean>()
    private val isSongFavLiveData = MutableLiveData<Boolean>()

    lateinit var binding: ActivityMainBinding

    fun mediaItemClicked(mediaItem: MediaBrowserCompat.MediaItem, extras: Bundle?) {
        transportControls()?.playFromMediaId(mediaItem.mediaId, extras)
    }

    fun transportControls() = PlaybackConnection.transportControls

    fun reloadQueueIds(ids: LongArray, type: String) {
        transportControls()?.sendCustomAction(
            UPDATE_QUEUE,
            bundleOf(QUEUE_LIST_KEY to ids, QUEUE_LIST_TYPE_KEY to type)
        )
    }

    fun isFav(id: Long): LiveData<Boolean> {
        launch {
            val isFav = withContext(IO) {
                favoritesRepository.favExist(id)
            }
            isFavLiveData.postValue(isFav)
        }
        return isFavLiveData
    }

    fun isAlbumFav(id: Long): LiveData<Boolean> {
        launch {
            val isFav = withContext(IO) {
                favoritesRepository.favExist(id)
            }
            isAlbumFavLiveData.postValue(isFav)
        }
        return isAlbumFavLiveData
    }

    fun isSongFav(id: Long): LiveData<Boolean> {
        launch {
            val isFav = withContext(IO) {
                favoritesRepository.songExist(id)
            }
            isSongFavLiveData.postValue(isFav)
        }
        return isSongFavLiveData
    }

    fun hideMiniPlayer() {
        binding.apply {
            bottomControls.isEnabled = false
            please(190, AccelerateInterpolator()) {
                animate(bottomControls) {
                    belowOf(mainContainer)
                }
            }.start()
        }
    }

    fun showMiniPlayer() {
        binding.apply {
            bottomControls.isEnabled = true
            please(190, AccelerateInterpolator()) {
                animate(bottomControls) {
                    bottomOfItsParent()
                }
            }.start()
        }
    }
}

