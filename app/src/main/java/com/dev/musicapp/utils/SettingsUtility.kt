package com.dev.musicapp.utils

import android.content.Context
import android.content.SharedPreferences
import com.dev.musicapp.utils.BeatConstants.AUTO_THEME
import com.dev.musicapp.utils.SortModes.AlbumModes.Companion.ALBUM_A_Z
import com.dev.musicapp.utils.SortModes.ArtistModes.Companion.ARTIST_A_Z
import com.dev.musicapp.utils.SortModes.SongModes.Companion.SONG_A_Z
import com.dev.musicapp.utils.SortModes.SongModes.Companion.SONG_TRACK

class SettingsUtility(context: Context) {

    private val sPreferences: SharedPreferences

    var startPageIndexSelected: Int
        get() = sPreferences.getInt(LAST_OPTION_SELECTED_KEY, 2)
        set(value) {
            setPreference(LAST_OPTION_SELECTED_KEY, value)
        }

    var songSortOrder: String
        get() = sPreferences.getString(SONG_SORT_ORDER_KEY, SONG_A_Z) ?: SONG_A_Z
        set(value) {
            setPreference(SONG_SORT_ORDER_KEY, value)
        }


    var albumSortOrder: String
        get() = sPreferences.getString(ALBUM_SORT_ORDER_KEY, ALBUM_A_Z) ?: ALBUM_A_Z
        set(value) {
            setPreference(ALBUM_SORT_ORDER_KEY, value)
        }

    var currentTheme: String
        get() = sPreferences.getString(CURRENT_THEME_KEY, AUTO_THEME) ?: AUTO_THEME
        set(value) {
            setPreference(CURRENT_THEME_KEY, value)
        }

    var albumSongSortOrder: String
        get() = sPreferences.getString(ALBUM_SONG_SORT_ORDER_KEY, SONG_TRACK) ?: SONG_TRACK
        set(value) {
            setPreference(ALBUM_SONG_SORT_ORDER_KEY, value)
        }

    var currentQueueInfo: String
        get() = sPreferences.getString(QUEUE_INFO_KEY, null) ?: "{}"
        set(value) {
            setPreference(QUEUE_INFO_KEY, value)
        }

    var currentQueueList: String
        get() = sPreferences.getString(QUEUE_LIST_KEY, null) ?: "[]"
        set(value) {
            setPreference(QUEUE_LIST_KEY, value)
        }

    var artistSortOrder: String
        get() = sPreferences.getString(ARTIST_SORT_ORDER_KEY, ARTIST_A_Z) ?: ARTIST_A_Z
        set(value) {
            setPreference(ARTIST_SORT_ORDER_KEY, value)
        }

    var didStop: Boolean
        get() = sPreferences.getBoolean(DID_STOP_KEY, false)
        set(value) {
            setPreference(DID_STOP_KEY, value)
        }

    private fun setPreference(key: String, value: String) {
        val editor = sPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    private fun setPreference(key: String, value: Int) {
        val editor = sPreferences.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    private fun setPreference(key: String, state: Boolean) {
        val editor = sPreferences.edit()
        editor.putBoolean(key, state)
        editor.apply()
    }

    companion object {
        private const val SHARED_PREFERENCES_FILE_NAME = "configs"
        private const val SONG_SORT_ORDER_KEY = "song_sort_order"
        private const val ALBUM_SORT_ORDER_KEY = "album_sort_order"
        private const val ALBUM_SONG_SORT_ORDER_KEY = "album_song_sort_order"
        private const val ARTIST_SORT_ORDER_KEY = "artist_sort_order"
        private const val LAST_OPTION_SELECTED_KEY = "last_option_selected"
        private const val CURRENT_THEME_KEY = "current_theme"
        private const val DID_STOP_KEY = "did_stop_key"

        const val QUEUE_INFO_KEY = "queue_info_key"
        const val QUEUE_LIST_KEY = "queue_list_key"
    }

    init {
        sPreferences = context.getSharedPreferences(
            SHARED_PREFERENCES_FILE_NAME,
            Context.MODE_PRIVATE
        )
    }
}