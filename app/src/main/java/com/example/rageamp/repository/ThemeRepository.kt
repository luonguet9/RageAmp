package com.example.rageamp.repository

import com.example.rageamp.data.data_source.pref.SharedPreferencesManager
import com.example.rageamp.utils.BLUE_THEME
import com.example.rageamp.utils.THEME

interface ThemeRepository {
	fun saveTheme(theme: String)
	fun getTheme(): String
}

class ThemeRepositoryImpl(
	private val sharedPreferencesManager: SharedPreferencesManager
) : ThemeRepository {
	override fun saveTheme(theme: String) {
		sharedPreferencesManager.put(THEME, theme)
	}
	
	override fun getTheme(): String {
		return sharedPreferencesManager.get(THEME, BLUE_THEME)
	}
}
