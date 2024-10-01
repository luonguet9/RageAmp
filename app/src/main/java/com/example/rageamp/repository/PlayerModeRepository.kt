package com.example.rageamp.repository

import com.example.rageamp.data.data_source.pref.SharedPreferencesManager
import com.example.rageamp.utils.REPEAT_MODE
import com.example.rageamp.utils.SHUFFLE_MODE

interface PlayerModeRepository {
	fun getCurrentRepeatMode(): Int
	fun getCurrentShuffleModeEnable(): Boolean
}

class PlayerModeRepositoryImpl(
	private val sharedPreferencesManager: SharedPreferencesManager
) : PlayerModeRepository {
	override fun getCurrentRepeatMode(): Int = sharedPreferencesManager.get(REPEAT_MODE, 0)
	
	
	override fun getCurrentShuffleModeEnable(): Boolean =
		sharedPreferencesManager.get(SHUFFLE_MODE, false)
}
