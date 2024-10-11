package com.example.rageamp.di

import android.content.Context
import androidx.room.Room
import com.example.rageamp.data.data_source.room.AppDatabase
import com.example.rageamp.data.data_source.room.dao.PlaylistDao
import com.example.rageamp.data.data_source.room.dao.SongDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
	@Provides
	@Singleton
	fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
		return Room.databaseBuilder(
			context,
			AppDatabase::class.java,
			AppDatabase.DATABASE_NAME
		).build()
	}
	
	@Provides
	@Singleton
	fun provideSongDao(appDatabase: AppDatabase): SongDao {
		return appDatabase.songDao()
	}
	
	@Provides
	@Singleton
	fun providePlaylistDao(appDatabase: AppDatabase): PlaylistDao {
		return appDatabase.playlistDao()
	}
}
