package com.example.rageamp.data.model

import java.io.Serializable

data class Song(
	val id: Long,
	val title: String?,
	val artist: String?,
	val duration: Long?,
	val data: String?,
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
