package com.example.rageamp.data.model

import java.io.Serializable

data class Song(
	val id: Long,
	val title: String?,
	val artist: String?,
	val duration: Long?,
	val data: String?,
	val albumArt: String?,
) : Serializable
