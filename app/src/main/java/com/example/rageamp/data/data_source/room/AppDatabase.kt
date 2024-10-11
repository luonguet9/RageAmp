package com.example.rageamp.data.data_source.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.rageamp.data.data_source.room.dao.PlaylistDao
import com.example.rageamp.data.data_source.room.dao.SongDao
import com.example.rageamp.data.model.Playlist
import com.example.rageamp.data.model.Song
import com.example.rageamp.data.model.PlaylistSongCrossRef

@Database(entities = [Song::class, Playlist::class, PlaylistSongCrossRef::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
	abstract fun songDao(): SongDao
	abstract fun playlistDao(): PlaylistDao
	
	companion object {
		const val DATABASE_NAME = "app_database"
	}
}
