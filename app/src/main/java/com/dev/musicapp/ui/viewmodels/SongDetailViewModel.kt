package com.dev.musicapp.ui.viewmodels


import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.dev.musicapp.ui.viewmodels.base.CoroutineViewModel
import com.dev.musicapp.models.MediaItemData
import com.dev.musicapp.models.PlaybackState
import com.dev.musicapp.models.Queue
import com.dev.musicapp.playback.PlaybackConnection
import com.dev.musicapp.repository.FavoritesRepository
import com.dev.musicapp.utils.BeatConstants.BIND_STATE_BOUND
import com.dev.musicapp.utils.BeatConstants.BIND_STATE_CANCELED
import com.dev.musicapp.utils.BeatConstants.SET_MEDIA_STATE
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SongDetailViewModel(
    private val favoritesRepository: FavoritesRepository,
    mediaPlaybackConnection: PlaybackConnection
) : CoroutineViewModel(Main) {

    private var state = BIND_STATE_CANCELED

    private val currentDataBase = MutableLiveData<MediaItemData>()
    val currentData: LiveData<MediaItemData> = currentDataBase

    private val lastDataBase = MutableLiveData<MediaItemData>()
    val lastData: LiveData<MediaItemData> = lastDataBase

    private val currentStateBase = MutableLiveData<PlaybackState>()
    val currentState: LiveData<PlaybackState> = currentStateBase

    private val queueDataBase = MutableLiveData<Queue>()
    val queueData: LiveData<Queue> = queueDataBase

    private val timeLiveData = MutableLiveData<Int>()
    val time: LiveData<Int> = timeLiveData

    private val rawData = MutableLiveData<ByteArray>().apply { value = byteArrayOf() }
    val raw: LiveData<ByteArray> = rawData

    private val isSongFavLiveData = MutableLiveData<Boolean>()
    private val lyrics: MutableLiveData<String> = MutableLiveData()

    private val playbackStateObserver = Observer<PlaybackStateCompat> { playbackState ->
        playbackState?.let {
            currentStateBase.postValue(PlaybackState.pullPlaybackState(it))
        }
    }

    private val nowMediaMetadataObserver = Observer<MediaMetadataCompat> { mediaMetaData ->
        mediaMetaData?.let {
            currentDataBase.postValue(MediaItemData.pullMediaMetadata(it) ?: return@let)
        }
    }

    private val lastMediaMetadataObserver = Observer<MediaMetadataCompat> { mediaMetaData ->
        mediaMetaData?.let {
            lastDataBase.postValue(MediaItemData.pullMediaMetadata(it) ?: return@let)
        }
    }

    private val queueDataObserver = Observer<Queue> { queueData ->
        queueData?.let {
            queueDataBase.postValue(queueData)
        }
    }

    private val mediaMediaConnection = mediaPlaybackConnection.also {
        it.playbackState.observeForever(playbackStateObserver)
        it.nowPlaying.observeForever(nowMediaMetadataObserver)
        it.lastPlayed.observeForever(lastMediaMetadataObserver)
        it.queueLiveData.observeForever(queueDataObserver)

        it.isConnected.observeForever { connected ->
            if (connected) {
                it.transportControls?.sendCustomAction(SET_MEDIA_STATE, null)
            }
        }
    }

    fun update(newTime: Int) {
        timeLiveData.postValue(newTime)
    }

    fun update(raw: ByteArray) {
        if (rawData.value == null) {
            rawData.postValue(raw)
        } else if (!rawData.value!!.contentEquals(raw)) rawData.postValue(raw)
    }

    fun update(bindState: String = BIND_STATE_CANCELED) {
        state = bindState
        if (state == BIND_STATE_BOUND) bindTime()
    }

    private fun bindTime() {
        GlobalScope.launch {
            while (true) {
                delay(100)
                mediaMediaConnection.mediaController ?: continue
                val newTime = mediaMediaConnection.mediaController?.playbackState!!.position
                if (state == BIND_STATE_BOUND) update(newTime.toInt())
                if (state == BIND_STATE_CANCELED) break
            }
        }
    }

    fun isSongFav(id: Long): LiveData<Boolean> {
        GlobalScope.launch {
            val isFav = withContext(IO) {
                favoritesRepository.songExist(id)
            }
            isSongFavLiveData.postValue(isFav)
        }
        return isSongFavLiveData
    }

    fun getLyrics(): LiveData<String> {
        return lyrics
    }

    fun updateLyrics(lyric: String? = null) {
        lyrics.postValue(lyric)
    }

    override fun onCleared() {
        super.onCleared()
        mediaMediaConnection.playbackState.removeObserver(playbackStateObserver)
        mediaMediaConnection.nowPlaying.removeObserver(nowMediaMetadataObserver)
        mediaMediaConnection.lastPlayed.removeObserver(lastMediaMetadataObserver)
    }
}