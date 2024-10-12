package com.example.rageamp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "song_table")
data class Song(
	@PrimaryKey(autoGenerate = true)
	val songId: Long,
	val title: String?,
	val artist: String?,
	val duration: Long?,
	val data: String?,
	val album: String?,
	val albumArt: String?,
	var mimeType: String? = "",
	val bitrate: Long? = 128,
	val year: Long? = null,
) : Serializable {
	 fun convertMimeTypeToExtension(mimeType: String) {
		this.mimeType = when (mimeType) {
			"audio/mpeg" -> "mp3"
			"audio/wav" -> "wav"
			"audio/x-wav" -> "wav"
			"audio/ogg" -> "ogg"
			"audio/flac" -> "flac"
			else -> "unknown"
		}
	}
}
