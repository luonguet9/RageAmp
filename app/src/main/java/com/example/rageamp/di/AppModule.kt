package com.example.rageamp.di

import android.content.ContentResolver
import android.content.Context
import com.example.rageamp.repository.SongRepository
import com.example.rageamp.repository.SongRepositoryImpl
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
}
