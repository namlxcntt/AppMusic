
package com.dev.musicapp.playback.players

import android.app.Application
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.PowerManager
import com.dev.musicapp.alias.OnCompletion
import com.dev.musicapp.alias.OnError
import com.dev.musicapp.alias.OnPrepared
import timber.log.Timber

interface BeatMediaPlayer {
    fun play()
    fun setSource(path: String): Boolean
    fun setSource(uri: Uri): Boolean
    fun prepare()
    fun seekTo(position: Int)
    fun isPrepared(): Boolean
    fun isPlaying(): Boolean
    fun position(): Int
    fun pause()
    fun stop()
    fun reset()
    fun release()
    fun onPrepared(prepared: OnPrepared<BeatMediaPlayer>)
    fun onError(error: OnError<BeatMediaPlayer>)
    fun onCompletion(completion: OnCompletion<BeatMediaPlayer>)
}

class BeatMediaPlayerImplementation(internal val context: Application) : BeatMediaPlayer,
    MediaPlayer.OnPreparedListener,
    MediaPlayer.OnErrorListener,
    MediaPlayer.OnCompletionListener {

    private var mediaPlayerBase: MediaPlayer? = null
    private val mediaPlayer: MediaPlayer
        get() {
            if (mediaPlayerBase == null) {
                mediaPlayerBase = createPlayer(this)
            }
            return mediaPlayerBase ?: throw IllegalStateException("Impossible")
        }

    private var isPrepared = false
    private var onPrepared: OnPrepared<BeatMediaPlayer> = {}
    private var onError: OnError<BeatMediaPlayer> = {}
    private var onCompletion: OnCompletion<BeatMediaPlayer> = {}

    override fun play() {
        mediaPlayer.start()
    }

    override fun setSource(path: String): Boolean {
        try {
            mediaPlayer.setDataSource(path)
        } catch (e: Exception) {
            onError(this, e)
            return false
        }
        return true
    }

    override fun setSource(uri: Uri): Boolean {
        try {
            mediaPlayer.setDataSource(context, uri)
        } catch (e: Exception) {
            onError(this, e)
            return false
        }
        return true
    }

    override fun prepare() {
        mediaPlayer.prepareAsync()
    }

    override fun seekTo(position: Int) {
        mediaPlayer.seekTo(position)
    }

    override fun isPrepared() = isPrepared

    override fun isPlaying() = mediaPlayer.isPlaying

    override fun position() = mediaPlayer.currentPosition

    override fun pause() {
        mediaPlayer.pause()
    }

    override fun stop() {
        mediaPlayer.stop()
    }

    override fun reset() {
        try {
            mediaPlayer.reset()
        } catch (ex: java.lang.IllegalStateException){
            Timber.e(ex)
        }
    }

    override fun release() {
        mediaPlayer.release()
    }

    /* Custom Media Player Callbacks */

    override fun onPrepared(prepared: OnPrepared<BeatMediaPlayer>) {
        this.onPrepared = prepared
    }

    override fun onError(error: OnError<BeatMediaPlayer>) {
        this.onError = error
    }

    override fun onCompletion(completion: OnCompletion<BeatMediaPlayer>) {
        this.onCompletion = completion
    }

    /* Media Player Callbacks */

    override fun onPrepared(mp: MediaPlayer?) {
        isPrepared = true
        onPrepared(this)
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        isPrepared = false
        return false
    }

    override fun onCompletion(mp: MediaPlayer?) {
        onCompletion(this)
    }
}

private fun createPlayer(owner: BeatMediaPlayerImplementation): MediaPlayer {
    return MediaPlayer().apply {
        setWakeMode(owner.context, PowerManager.PARTIAL_WAKE_LOCK)
        val attr = AudioAttributes.Builder().apply {
            setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            setUsage(AudioAttributes.USAGE_MEDIA)
        }.build()
        setAudioAttributes(attr)
        setOnPreparedListener(owner)
        setOnCompletionListener(owner)
        setOnErrorListener(owner)
    }
}