package com.ader.codenames.di

import android.content.Context
import androidx.room.Room
import com.ader.codenames.data.AppDataBase
import com.ader.codenames.data.Constants
import com.ader.codenames.data.dao.GameDao
import com.ader.codenames.data.dao.GameWordDao
import com.ader.codenames.data.dao.TeamDao
import com.ader.codenames.data.dao.WordDao
import com.ader.codenames.data.preferences.PreferenceRepository
import com.ader.codenames.data.preferences.PreferenceRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {
    @Provides
    @Singleton
    fun provideGameDao(appDataBase: AppDataBase): GameDao{
        return appDataBase.gameDao()
    }

    @Provides
    @Singleton
    fun provideGameWordDao(appDataBase: AppDataBase): GameWordDao {
        return appDataBase.gameWordDao()
    }

    @Provides
    @Singleton
    fun provideTeamDao(appDataBase: AppDataBase): TeamDao {
        return appDataBase.teamDao()
    }

    @Provides
    @Singleton
    fun provideWordDao(appDataBase: AppDataBase): WordDao {
        return appDataBase.wordDao()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDataBase {
        return Room.databaseBuilder(
            appContext,
            AppDataBase::class.java,
            Constants.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun providePreference(@ApplicationContext appContext: Context): PreferenceRepository{
        return PreferenceRepositoryImpl(appContext)
    }
}