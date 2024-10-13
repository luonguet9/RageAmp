package com.example.rageamp.repository

import com.example.rageamp.data.data_source.pref.SharedPreferencesManager
import com.example.rageamp.data.data_source.room.dao.PlaylistDao
import com.example.rageamp.data.model.Playlist
import com.example.rageamp.data.model.PlaylistSongCrossRef
import com.example.rageamp.data.model.Song
import com.example.rageamp.utils.IS_FAVORITE_PLAYLIST_EXISTS
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface PlaylistRepository {
	fun getPlaylists(): Flow<List<Playlist>>
	suspend fun insertPlaylists(vararg playlists: Playlist)
	suspend fun renamePlaylist(playlist: Playlist, newName: String)
	suspend fun deletePlaylists(vararg playlists: Playlist)
	fun isFavoritePlaylistExists(): Boolean
	fun saveFavoritePlaylistExists()
	fun getAllSongsInPlaylist(playlist: Playlist): Flow<List<Song>>
	suspend fun addSongToPlaylist(song: Song, playlistId: Int): Boolean
	suspend fun removeSongFromPlaylist(song: Song, playlistId: Int)
	suspend fun isSongExistsInPlaylist(song: Song, playlistId: Int): Boolean
}

class PlaylistRepositoryImpl @Inject constructor(
	private val playlistDao: PlaylistDao,
	private val sharedPreferencesManager: SharedPreferencesManager,
) : PlaylistRepository {
	
	override fun getPlaylists(): Flow<List<Playlist>> {
		return playlistDao.getAllPlaylistsWithSongs().map { playlistsWithSongs ->
			playlistsWithSongs.map { playlistWithSongs ->
				Playlist(
					playlistId = playlistWithSongs.playlist.playlistId,
					name = playlistWithSongs.playlist.name,
					songs = playlistWithSongs.songs.toMutableList()
				)
			}
		}
	}
	
	override suspend fun insertPlaylists(vararg playlists: Playlist) {
		playlistDao.insertPlaylists(*playlists)
	}
	
	override suspend fun renamePlaylist(playlist: Playlist, newName: String) {
		val newPlaylist = playlist.copy(name = newName)
		playlistDao.updatePlaylists(newPlaylist)
	}
	
	override suspend fun deletePlaylists(vararg playlists: Playlist) {
		playlistDao.deletePlaylists(*playlists)
	}
	
	override fun isFavoritePlaylistExists(): Boolean =
		sharedPreferencesManager.get(IS_FAVORITE_PLAYLIST_EXISTS, false)
	
	override fun saveFavoritePlaylistExists() {
		sharedPreferencesManager.put(IS_FAVORITE_PLAYLIST_EXISTS, true)
	}
	
	override fun getAllSongsInPlaylist(playlist: Playlist): Flow<List<Song>> {
		return playlistDao.getPlaylistWithSongs(playlistId = playlist.playlistId)
			.map { it.songs }  // Convert PlaylistWithSongs to List<Song>
	}
	
	override suspend fun addSongToPlaylist(song: Song, playlistId: Int): Boolean {
		if (isSongExistsInPlaylist(song, playlistId)) return false
		val crossRef = PlaylistSongCrossRef(songId = song.songId, playlistId = playlistId)
		playlistDao.insertPlaylistSongCrossRef(crossRef)
		return true
	}
	
	override suspend fun removeSongFromPlaylist(song: Song, playlistId: Int) {
		val crossRef = PlaylistSongCrossRef(songId = song.songId, playlistId = playlistId)
		playlistDao.deletePlaylistSongCrossRef(crossRef)
	}
	
	override suspend fun isSongExistsInPlaylist(song: Song, playlistId: Int): Boolean {
		return playlistDao.isSongExistsInPlaylist(song.songId, playlistId) > 0
	}
}
