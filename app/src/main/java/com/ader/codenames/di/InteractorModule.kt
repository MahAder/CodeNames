package com.ader.codenames.di

import android.content.Context
import com.ader.codenames.data.dao.GameDao
import com.ader.codenames.data.dao.GameWordDao
import com.ader.codenames.data.dao.TeamDao
import com.ader.codenames.data.dao.WordDao
import com.ader.codenames.data.preferences.PreferenceRepository
import com.ader.codenames.domain.interactor.game.GameInteractor
import com.ader.codenames.domain.interactor.game.GameInteractorImpl
import com.ader.codenames.domain.interactor.game.GameManager
import com.ader.codenames.domain.interactor.game.GameManagerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class InteractorModule {
    @Singleton
    @Provides
    fun provideGameInteractor(
        gameDao: GameDao,
        gameWordDao: GameWordDao,
        teamDao: TeamDao,
        wordDao: WordDao
    ): GameInteractor {
        return GameInteractorImpl(gameDao, teamDao, gameWordDao, wordDao)
    }

    @Singleton
    @Provides
    fun provideGameManager(
        gameInteractor: GameInteractor,
        @ApplicationContext context: Context,
        preferenceRepository: PreferenceRepository
    ): GameManager {
        return GameManagerImpl(gameInteractor, context, preferenceRepository)
    }
}