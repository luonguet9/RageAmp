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
		getSongsFromDevice()
	}
	
	private val _songs = MutableStateFlow<List<Song>>(emptyList())
	val songs: StateFlow<List<Song>> = _songs
	
	private fun getSongsFromDevice() {
		viewModelScope.launch {
			val songs = songRepository.getSongsFromDevice()
			Logger.d(TAG, "viewModelScope songs: $songs")
			_songs.value = songs
		}
	}
	
	companion object {
		private val TAG = SongViewModel::class.simpleName
	}
}
