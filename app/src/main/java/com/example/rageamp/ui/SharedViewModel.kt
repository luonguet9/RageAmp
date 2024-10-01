package com.example.rageamp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rageamp.data.model.Song
import com.example.rageamp.repository.PlayerModeRepository
import com.example.rageamp.repository.ThemeRepository
import com.example.rageamp.utils.enums.NavigationAction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
	private val themeRepository: ThemeRepository,
	private val playerModeRepository: PlayerModeRepository
) : ViewModel() {
	private val _navigationAction = MutableLiveData<NavigationAction>()
	val navigationAction: LiveData<NavigationAction> get() = _navigationAction
	
	private val _currentSong = MutableLiveData<Song>()
	val currentSong: LiveData<Song> get() = _currentSong
	
	private val _currentSongs = MutableStateFlow<List<Song>>(emptyList())
	val currentSongs: StateFlow<List<Song>> get() = _currentSongs
	
	private val _isPlaying = MutableLiveData(false)
	val isPlaying: LiveData<Boolean>
		get() = _isPlaying
	
	private val _shuffleModeEnabled = MutableLiveData(false)
	val shuffleModeEnabled: LiveData<Boolean>
		get() = _shuffleModeEnabled
	
	private val _repeatMode = MutableLiveData(0)
	val repeatMode: LiveData<Int>
		get() = _repeatMode
	
	fun navigate(action: NavigationAction) {
		_navigationAction.value = action
	}
	
	fun resetNavigation() {
		_navigationAction.value = null
	}
	
	fun setCurrentSong(song: Song) {
		if (_currentSong.value != song) {
			_currentSong.value = song
		}
	}
	
	fun setPlayingStatus(isPlaying: Boolean) {
		if (_isPlaying.value != isPlaying) {
			_isPlaying.value = isPlaying
		}
	}
	
	fun setCurrentSongs(songs: List<Song>) {
		if (_currentSongs.value != songs) {
			viewModelScope.launch(Dispatchers.IO) {
				_currentSongs.emit(songs)
			}
		}
	}
	
	fun getShuffleModeEnabled() {
		val currentShuffleModeEnable = playerModeRepository.getCurrentShuffleModeEnable()
		setShuffleModeEnabled(currentShuffleModeEnable)
	}
	
	fun setShuffleModeEnabled(shuffleModeEnabled: Boolean) {
		if (_shuffleModeEnabled.value != shuffleModeEnabled) {
			_shuffleModeEnabled.value = shuffleModeEnabled
		}
	}
	
	fun getRepeatMode() {
		val currentRepeatMode = playerModeRepository.getCurrentRepeatMode()
		setRepeatMode(currentRepeatMode)
	}
	
	fun setRepeatMode(repeatMode: Int) {
		if (_repeatMode.value != repeatMode) {
			_repeatMode.value = repeatMode
		}
	}
	
	fun saveTheme(theme: String) {
		themeRepository.saveTheme(theme)
	}
	
	fun getTheme(): String {
		return themeRepository.getTheme()
	}
}
