package com.dev.musicapp.utils

import com.dev.musicapp.models.MediaItemData
import com.dev.musicapp.repository.SongsRepository
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.audio.exceptions.CannotReadException
import org.jaudiotagger.audio.exceptions.CannotWriteException
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.TagException
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.util.*

object LyricsHelper {
    fun getEmbeddedLyrics(
        songsRepository: SongsRepository,
        mediaItemData: MediaItemData,
        isSync: Boolean = true
    ): String? {
        val lyrics = StringBuilder()
        val file = File(Objects.requireNonNull(songsRepository.getPath(mediaItemData.id)))
        try {
            val tag = if (isSync) FieldKey.CUSTOM1 else FieldKey.LYRICS
            val audioFile = AudioFileIO.read(file)

            lyrics.append(audioFile.tag.getFirst(tag))
        } catch (ex: CannotReadException) {
            Timber.e(ex)
        } catch (ex: IOException) {
            Timber.e(ex)
        } catch (ex: TagException) {
            Timber.e(ex)
        } catch (ex: ReadOnlyFileException) {
            Timber.e(ex)
        } catch (ex: InvalidAudioFrameException) {
            Timber.e(ex)
        } catch (ex: IllegalArgumentException) {
            Timber.e(ex)
        }
        if (isSync && lyrics.toString().isEmpty()) return getEmbeddedLyrics(
            songsRepository,
            mediaItemData,
            false
        )
        return if (lyrics.toString().isEmpty()) null else lyrics.toString()
    }

    fun setEmbeddedLyrics(
        songsRepository: SongsRepository,
        id: Long,
        lyrics: String,
        isSync: Boolean = false
    ): Boolean {
        val file = File(Objects.requireNonNull(songsRepository.getPath(id)))
        try {
            val audioFile = AudioFileIO.read(file)
            val tag = if (isSync) FieldKey.CUSTOM1 else FieldKey.LYRICS

            audioFile.tag.setField(tag, lyrics)
            audioFile.commit()
            return true
        } catch (ex: CannotReadException) {
            Timber.e(ex)
        } catch (ex: IOException) {
            Timber.e(ex)
        } catch (ex: TagException) {
            Timber.e(ex)
        } catch (ex: ReadOnlyFileException) {
            Timber.e(ex)
        } catch (ex: InvalidAudioFrameException) {
            Timber.e(ex)
        } catch (ex: IllegalArgumentException) {
            Timber.e(ex)
        } catch (ex: CannotWriteException) {
            Timber.e(ex)
        }
        return false
    }
}