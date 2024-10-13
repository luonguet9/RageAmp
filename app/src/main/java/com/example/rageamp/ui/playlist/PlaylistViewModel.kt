package com.example.rageamp.ui.playlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rageamp.data.model.Playlist
import com.example.rageamp.data.model.Song
import com.example.rageamp.repository.PlaylistRepository
import com.example.rageamp.utils.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PlaylistViewModel @Inject constructor(
	private val playlistRepository: PlaylistRepository
) : ViewModel() {
	val playlists: StateFlow<List<Playlist>> = playlistRepository.getPlaylists()
		.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
	
	var playlist: Playlist? = null
	
	fun getAllSongsInPlaylist(): Flow<List<Song>>? {
		return playlist?.let {
			playlistRepository.getAllSongsInPlaylist(it)
		}
	}
	
	fun insertPlaylists(vararg playlists: Playlist) {
		viewModelScope.launch {
			playlistRepository.insertPlaylists(*playlists)
		}
	}
	
	fun renamePlaylist(playlist: Playlist, newName: String) {
		viewModelScope.launch {
			playlistRepository.renamePlaylist(playlist, newName)
		}
	}
	
	fun deletePlaylists(vararg playlists: Playlist) {
		viewModelScope.launch {
			playlistRepository.deletePlaylists(*playlists)
		}
	}
	
	fun addSongToPlaylist(song: Song, playlist: Playlist, onResult: (Boolean) -> Unit) {
		viewModelScope.launch(Dispatchers.IO) {
			val success = playlistRepository.addSongToPlaylist(song, playlist.playlistId)
			withContext(Dispatchers.Main) {
				onResult(success)
			}
		}
	}
	
	fun removeSongFromPlaylist(song: Song, playlist: Playlist, onResult: () -> Unit) {
		viewModelScope.launch(Dispatchers.IO) {
			playlistRepository.removeSongFromPlaylist(song, playlist.playlistId)
			withContext(Dispatchers.Main) {
				onResult()
			}
		}
		this@PlaylistViewModel.playlist?.songs?.remove(song)
	}
	
	fun checkAndInsertFavoritePlaylist() {
		if (!playlistRepository.isFavoritePlaylistExists()) {
			viewModelScope.launch {
				Logger.d("Insert Favorite playlist")
				playlistRepository.insertPlaylists(Playlist(name = "Favorite"))
				playlistRepository.saveFavoritePlaylistExists()
			}
		}
	}
	
}
