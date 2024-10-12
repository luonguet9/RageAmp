package com.example.rageamp.ui.album

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rageamp.data.model.Album
import com.example.rageamp.repository.AlbumRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumViewModel @Inject constructor(
	private val albumRepository: AlbumRepository
) : ViewModel() {
	private val _albums = MutableStateFlow<List<Album>>(emptyList())
	val albums: StateFlow<List<Album>> get() = _albums
	
	init {
		fetchAlbums()
	}
	
	private fun fetchAlbums() {
		viewModelScope.launch {
			albumRepository.getAlbums().collect { albumList ->
				_albums.value = albumList
			}
		}
	}
	
}
