package com.example.rageamp.data.data_source.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.rageamp.data.model.Song
import kotlinx.coroutines.flow.Flow

@Dao
interface SongDao {
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertSongs(vararg song: Song)
	
	@Update
	suspend fun updateSongs(vararg song: Song)
	
	@Delete
	suspend fun deleteSongs(vararg song: Song)
	
	@Query("SELECT * FROM song_table")
	fun getAllSongs(): Flow<List<Song>>
}
