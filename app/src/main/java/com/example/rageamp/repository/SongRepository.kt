package com.example.rageamp.repository

import android.content.ContentResolver
import android.net.Uri
import android.provider.MediaStore
import com.example.rageamp.data.model.Song
import com.example.rageamp.utils.Logger

interface SongRepository {
	suspend fun getSongsFromDevice(): List<Song>
}

class SongRepositoryImpl(
	private val contentResolver: ContentResolver,
) : SongRepository {
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
			MediaStore.Audio.Media.ALBUM_ID,
			MediaStore.Audio.Media.MIME_TYPE,
			MediaStore.Audio.Media.BITRATE,
			MediaStore.Audio.Media.YEAR,
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
				val mimeType =
					it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE))
				val bitrate = it.getLong(it.getColumnIndexOrThrow(MediaStore.Audio.Media.BITRATE))
				val year = it.getLong(it.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR))
				val uriAlbumArt = Uri.parse("content://media/external/audio/albumart")
				val albumArt = Uri.withAppendedPath(uriAlbumArt, albumId.toString()).toString()
				val song = Song(
					id = id,
					title = title,
					artist = artist,
					duration = duration,
					data = data,
					albumArt = albumArt,
					bitrate = bitrate / 1000,
					year = year,
				).apply {
					convertMimeTypeToExtension(mimeType)
				}
				songs.add(song)
			}
		}
		Logger.d(TAG, "SongRepository songs: $songs")
		return songs
	}
	
	companion object {
		private val TAG = SongRepository::class.simpleName
	}
}


