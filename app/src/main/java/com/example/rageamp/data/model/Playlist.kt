package com.example.rageamp.data.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "playlist_table")
data class Playlist(
	@PrimaryKey(autoGenerate = true)
	var playlistId: Int = 0,
	var name: String = "",
	@Ignore
	val songs: MutableList<Song> = mutableListOf(),
)
