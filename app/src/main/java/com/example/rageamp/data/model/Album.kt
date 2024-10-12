package com.example.rageamp.data.model

import java.io.Serializable

data class Album(
	val name: String?,
	val albumArt: String?,
	val songs: List<Song>
) : Serializable
