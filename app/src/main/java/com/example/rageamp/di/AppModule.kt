package com.example.rageamp.di

import android.content.ContentResolver
import android.content.Context
import com.example.rageamp.data.data_source.pref.SharedPreferencesManager
import com.example.rageamp.repository.PlayerModeRepository
import com.example.rageamp.repository.PlayerModeRepositoryImpl
import com.example.rageamp.repository.SongRepository
import com.example.rageamp.repository.SongRepositoryImpl
import com.example.rageamp.repository.ThemeRepository
import com.example.rageamp.repository.ThemeRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
	@Provides
	@Singleton
	fun provideContentResolver(@ApplicationContext context: Context): ContentResolver {
		return context.contentResolver
	}
	
	@Singleton
	@Provides
	fun provideSongRepository(
		contentResolver: ContentResolver,
	): SongRepository {
		return SongRepositoryImpl(contentResolver)
	}
	
	@Singleton
	@Provides
	fun provideSharedPreferencesManager(@ApplicationContext context: Context): SharedPreferencesManager {
		return SharedPreferencesManager(context)
	}
	
	@Singleton
	@Provides
	fun provideThemeRepository(
		sharedPreferencesManager: SharedPreferencesManager
	): ThemeRepository {
		return ThemeRepositoryImpl(sharedPreferencesManager)
	}
	
	@Singleton
	@Provides
	fun providePlayerModeRepository(
		sharedPreferencesManager: SharedPreferencesManager
	): PlayerModeRepository {
		return PlayerModeRepositoryImpl(sharedPreferencesManager)
	}
}
