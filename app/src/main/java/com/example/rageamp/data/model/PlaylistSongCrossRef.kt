package com.example.rageamp.data.model

import androidx.room.Entity

@Entity(tableName = "playlist_song_cross_ref", primaryKeys = ["songId", "playlistId"])
data class PlaylistSongCrossRef(
	val songId: Long,
	val playlistId: Int
)
