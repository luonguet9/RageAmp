package com.example.rageamp.repository

import com.example.rageamp.data.model.Album
import com.example.rageamp.data.model.Song
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface AlbumRepository {
	suspend fun getAlbums(): Flow<List<Album>>
}

class AlbumRepositoryImpl(private val songRepository: SongRepository) : AlbumRepository {
	override suspend fun getAlbums(): Flow<List<Album>> {
		return songRepository.getAllSongs().map { songs ->
			createAlbumList(songs)
		}
	}
	
	private fun createAlbumList(songs: List<Song>): List<Album> {
		return songs.filter { it.album != null }
			.groupBy { it.album }
			.map { (albumName, songsInAlbum) ->
				val albumArt = songsInAlbum.firstOrNull()?.albumArt
				Album(albumName, albumArt, songsInAlbum)
			}
	}
	
}

