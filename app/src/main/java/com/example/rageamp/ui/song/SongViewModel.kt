package com.example.rageamp.ui.song

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rageamp.data.model.Song
import com.example.rageamp.repository.SongRepository
import com.example.rageamp.utils.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SongViewModel @Inject constructor(
	private val songRepository: SongRepository
) : ViewModel() {
	init {
		viewModelScope.launch {
			songRepository.getAllSongs().collect { songs ->
				Logger.d(TAG, "collect songs: $songs")
				_songs.value = songs
			}
		}
	}
	
	private val _songs = MutableStateFlow<List<Song>>(emptyList())
	val songs: StateFlow<List<Song>> = _songs
	
	fun sortSongsByTitleAZ() {
		val sortedList = _songs.value.sortedBy { it.title }
		_songs.value = sortedList
		Logger.d(TAG, "Songs sorted by Title (A-Z): $sortedList")
	}
	
	fun sortSongsByTitleZA() {
		val sortedList = _songs.value.sortedByDescending { it.title }
		_songs.value = sortedList
		Logger.d(TAG, "Songs sorted by Title (Z-A): $sortedList")
	}
	
	fun sortSongsByArtist() {
		val sortedList = _songs.value.sortedBy { it.artist }
		_songs.value = sortedList
		Logger.d(TAG, "Songs sorted by Artist: $sortedList")
	}
	
	fun sortSongsByDuration() {
		val sortedList = _songs.value.sortedBy { it.duration }
		_songs.value = sortedList
		Logger.d(TAG, "Songs sorted by Duration: $sortedList")
	}
	
	companion object {
		private val TAG = SongViewModel::class.simpleName
	}
}
