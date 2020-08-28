
package com.dev.musicapp.repository

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.dev.musicapp.db.DBHelper
import com.dev.musicapp.extensions.optimizeReadOnlyList
import com.dev.musicapp.extensions.toList
import com.dev.musicapp.models.Favorite
import com.dev.musicapp.models.Song
import com.dev.musicapp.utils.BeatConstants.FAVORITE_ID
import com.dev.musicapp.utils.BeatConstants.FAVORITE_TYPE

interface FavoritesRepository {
    fun createFavorite(favorite: Favorite): Int
    fun addSongByFavorite(idFavorite: Long, songs: List<Song>): Int
    fun deleteSongByFavorite(idFavorite: Long, ids: LongArray): Int
    fun deleteSongs(ids: LongArray): Int
    fun deleteFavorites(ids: LongArray): Int
    fun updateFavoriteCount(parentId: Long, id: Long): Int
    fun getFavorite(id: Long): Favorite
    fun getFavorites(): List<Favorite>
    fun getSongsForFavorite(id: Long): List<Song>
    fun songExist(id: Long): Boolean
    fun favExist(id: Long): Boolean
}

class FavoritesRepositoryImplementation(context: Context) : DBHelper(context),
    FavoritesRepository {

    companion object {
        const val TABLE_FAVORITES = "favorites"
        const val TABLE_SONGS = "favorite_songs"

        const val COLUMN_ID = "id"

        const val COLUMN_FAVORITE = "favorite"
        const val COLUMN_SONG_COUNT = "song_count"
        const val COLUMN_TITLE = "title"
        const val COLUMN_TYPE = "type"

        const val COLUMN_ARTIST = "artist"
        const val COLUMN_ALBUM = "album"
        const val COLUMN_DURATION = "duration"
        const val COLUMN_TRACK = "track_num"
        const val COLUMN_ARTIST_ID = "artist_id"
        const val COLUMN_ALBUM_ID = "album_id"
        const val COLUMN_YEAR = "year"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(getCreateFavoritesQuery())
        db?.execSQL(getCreateSongsQuery())
        db?.execSQL(getPlusTriggerQuery())
        db?.execSQL(getMinusTriggerQuery())
        db?.execSQL(getDeleteSongs())
        db?.execSQL(getFavoriteCoverIdTrigger())
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_FAVORITES")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_SONGS")
        onCreate(db)
    }

    override fun createFavorite(favorite: Favorite): Int {
        return insertRow(TABLE_FAVORITES, favorite.columnNames(), favorite.values())
    }

    override fun addSongByFavorite(idFavorite: Long, songs: List<Song>): Int {
        val array = songs.map {
            it.playListId = idFavorite
            val contentValues = ContentValues()
            val values = it.values()
            it.columns(FAVORITE_TYPE).mapIndexed { i, column ->
                contentValues.put(column, values[i])
            }
            contentValues
        }
        return bulkInsert(TABLE_SONGS, array.toTypedArray())
    }

    override fun deleteSongByFavorite(idFavorite: Long, ids: LongArray): Int {
        var resp = 0
        for (id in ids) {
            resp += deleteRow(
                TABLE_SONGS,
                "$COLUMN_ID = ? AND $COLUMN_FAVORITE = ?",
                arrayOf("$id", "$idFavorite")
            )
        }
        return resp
    }

    override fun deleteSongs(ids: LongArray): Int {
        var resp = 0
        for (id in ids) {
            resp += deleteRow(TABLE_SONGS, "$COLUMN_ID = ?", arrayOf("$id"))
        }
        return resp
    }

    override fun deleteFavorites(ids: LongArray): Int {
        var resp = 0
        for (id in ids) {
            resp += deleteRow(TABLE_FAVORITES, "$COLUMN_ID = ?", arrayOf("$id"))
        }
        return resp
    }

    override fun updateFavoriteCount(parentId: Long, id: Long): Int {
        val resp = deleteSongs(longArrayOf(id))
        if (resp == 0) {
            return updateRow(
                TABLE_FAVORITES,
                ContentValues().apply {
                    put(
                        COLUMN_SONG_COUNT,
                        getFavorite(parentId).songCount - 1
                    )
                },
                "$COLUMN_ID = ?",
                arrayOf("$parentId")
            )
        }
        return resp
    }

    override fun getFavorite(id: Long): Favorite {
        val cursor = getRow(TABLE_FAVORITES, "*", "$COLUMN_ID = ?", arrayOf(id.toString()))
        if (cursor.isClosed) return Favorite()
        cursor.use {
            return if (it.moveToFirst()) {
                Favorite.fromCursor(it)
            } else {
                Favorite()
            }
        }
    }

    override fun getFavorites(): List<Favorite> {
        val cursor = getRow(TABLE_FAVORITES, "*", "$COLUMN_SONG_COUNT > ?", arrayOf("0"), COLUMN_ID)
        if (cursor.isClosed) return emptyList()
        return cursor.toList(true) {
            Favorite.fromCursor(cursor)
        }.toList().optimizeReadOnlyList()
    }

    override fun getSongsForFavorite(id: Long): List<Song> {
        val cursor = getRow(TABLE_SONGS, "*", "$COLUMN_FAVORITE = ?", arrayOf("$id"))
        return cursor.toList(true, Song.Companion::createFromPlaylistCursor)
    }

    override fun songExist(id: Long): Boolean {
        val cursor = getRow(
            TABLE_SONGS,
            "*",
            "$COLUMN_ID = ? AND $COLUMN_FAVORITE = ?",
            arrayOf("$id", "$FAVORITE_ID")
        )
        cursor.use {
            return it.moveToFirst()
        }
    }

    override fun favExist(id: Long): Boolean {
        val cursor = getRow(TABLE_FAVORITES, "*", "$COLUMN_ID = ?", arrayOf("$id"))
        cursor.use {
            return it.moveToFirst()
        }
    }

    private fun getCreateFavoritesQuery(): String {
        return "CREATE TABLE $TABLE_FAVORITES (" +
                "$COLUMN_ID INTEGER PRIMARY KEY, " +
                "$COLUMN_TITLE TEXT, " +
                "$COLUMN_ARTIST TEXT, " +
                "$COLUMN_ARTIST_ID INTEGER, " +
                "$COLUMN_YEAR INTEGER, " +
                "$COLUMN_SONG_COUNT INTEGER, " +
                "$COLUMN_TYPE TEXT" +
                ")"
    }

    private fun getCreateSongsQuery(): String {
        return "CREATE TABLE $TABLE_SONGS (" +
                "$COLUMN_ID INTEGER, " +
                "$COLUMN_TITLE TEXT, " +
                "$COLUMN_ARTIST TEXT, " +
                "$COLUMN_ALBUM TEXT, " +
                "$COLUMN_DURATION INTEGER, " +
                "$COLUMN_TRACK INTEGER, " +
                "$COLUMN_ARTIST_ID INTEGER, " +
                "$COLUMN_ALBUM_ID INTEGER, " +
                "$COLUMN_FAVORITE INTEGER, " +
                "FOREIGN KEY($COLUMN_FAVORITE) REFERENCES FAVORITES($COLUMN_ID), " +
                "PRIMARY KEY($COLUMN_ID, $COLUMN_FAVORITE)" +
                ")"
    }

    private fun getPlusTriggerQuery(): String {
        return "CREATE TRIGGER PLUS_SONG_COUNT\n" +
                "AFTER INSERT ON $TABLE_SONGS\n" +
                "BEGIN\n" +
                "    UPDATE $TABLE_FAVORITES SET $COLUMN_SONG_COUNT = $COLUMN_SONG_COUNT + 1 WHERE $COLUMN_ID = NEW.$COLUMN_FAVORITE;\n" +
                "END"
    }

    private fun getMinusTriggerQuery(): String {
        return "CREATE TRIGGER MINUS_SONG_COUNT\n" +
                "AFTER DELETE ON $TABLE_SONGS\n" +
                "BEGIN\n" +
                "    UPDATE $TABLE_FAVORITES SET $COLUMN_SONG_COUNT = $COLUMN_SONG_COUNT - 1 WHERE $COLUMN_ID = OLD.$COLUMN_FAVORITE;\n" +
                "END"
    }

    private fun getDeleteSongs(): String {
        return "CREATE TRIGGER DELETE_SONGS\n" +
                "AFTER DELETE ON $TABLE_FAVORITES\n" +
                "BEGIN\n" +
                "    DELETE FROM $TABLE_SONGS WHERE $COLUMN_FAVORITE = OLD.$COLUMN_ID;\n" +
                "END"
    }

    private fun getFavoriteCoverIdTrigger(): String {
        return "CREATE TRIGGER ${TABLE_FAVORITES}_COVER_ID\n" +
                "AFTER UPDATE ON $TABLE_FAVORITES\n" +
                "BEGIN\n" +
                "   UPDATE $TABLE_FAVORITES " +
                "       SET $COLUMN_ARTIST_ID = (" +
                "           SELECT $COLUMN_ALBUM_ID " +
                "           FROM $TABLE_SONGS LIMIT 1) " +
                "           WHERE $COLUMN_ID = OLD.$COLUMN_ID;\n" +
                "END"
    }
}