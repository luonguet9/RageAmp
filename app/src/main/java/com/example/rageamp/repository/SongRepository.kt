package com.example.rageamp.repository

import android.content.ContentResolver
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.example.rageamp.data.model.Song

interface SongRepository {
	suspend fun getSongsFromDevice() : List<Song>
}
class SongRepositoryImpl(
	private val contentResolver: ContentResolver,
) : SongRepository{
	override suspend fun getSongsFromDevice(): List<Song> {
		val songs = mutableListOf<Song>()
		val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
		val selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0"
		val sortOrder = MediaStore.Audio.Media.TITLE + " ASC"
		val projection = arrayOf(
			MediaStore.Audio.Media._ID,
			MediaStore.Audio.Media.TITLE,
			MediaStore.Audio.Media.ARTIST,
			MediaStore.Audio.Media.DURATION,
			MediaStore.Audio.Media.DATA,
			MediaStore.Audio.Media.ALBUM_ID
		)
		val cursor = contentResolver.query(uri, projection, selection, null, sortOrder)
		cursor?.use {
			while (cursor.moveToNext()) {
				val id = it.getLong(it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID))
				val title = it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE))
				val artist = it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST))
				val duration = it.getLong(it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))
				val data = it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))
				val albumId = it.getLong(it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID))
				val uriAlbumArt = Uri.parse("content://media/external/audio/albumart")
				val albumArt = Uri.withAppendedPath(uriAlbumArt, albumId.toString()).toString()
				val song = Song(id, title, artist, duration, data, albumArt)
				songs.add(song)
			}
		}
		Log.d("CHECK_CHECK", "SongRepository songs: $songs")
		return songs
	}
}


