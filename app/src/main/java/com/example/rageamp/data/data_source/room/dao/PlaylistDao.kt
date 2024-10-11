package com.example.rageamp.data.data_source.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.rageamp.data.model.Playlist
import com.example.rageamp.data.model.PlaylistWithSongs
import com.example.rageamp.data.model.SongWithPlaylists
import com.example.rageamp.data.model.PlaylistSongCrossRef
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertPlaylists(vararg playlist: Playlist)
	
	@Update
	suspend fun updatePlaylists(vararg playlist: Playlist)
	
	@Delete
	suspend fun deletePlaylists(vararg playlist: Playlist)
	
	@Query("SELECT * FROM playlist_table")
	fun getPlaylists(): Flow<List<Playlist>>
	
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertPlaylistSongCrossRef(crossRef: PlaylistSongCrossRef)
	
	@Delete
	suspend fun deletePlaylistSongCrossRef(crossRef: PlaylistSongCrossRef)
	
	@Transaction
	@Query("SELECT * FROM playlist_table")
	fun getAllPlaylistsWithSongs(): Flow<List<PlaylistWithSongs>>
	
	@Transaction
	@Query("SELECT * FROM playlist_table WHERE playlistId = :playlistId")
	fun getPlaylistWithSongs(playlistId: Int): Flow<PlaylistWithSongs>
	
	@Transaction
	@Query("SELECT * FROM song_table WHERE songId = :songId")
	fun getSongWithPlaylists(songId: Long): SongWithPlaylists
	
	@Query("SELECT COUNT(*) FROM playlist_table WHERE name = :name")
	fun isPlaylistNameExists(name: String): Int
	
	@Query("SELECT COUNT(*) FROM playlist_song_cross_ref WHERE songId = :songId AND playlistId = :playlistId")
	fun isSongExistsInPlaylist(songId: Long, playlistId: Int): Int
}
