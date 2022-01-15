package com.ader.codenames.domain.interactor.game

import com.ader.codenames.data.model.GameDBModel
import com.ader.codenames.domain.model.GameModel
import com.ader.codenames.domain.model.GameWordModel

interface GameInteractor {
    suspend fun createNewGame(masterKey: String): GameModel

    suspend fun getActiveGame(): GameModel?

    suspend fun updateGame(gameDBModel: GameDBModel)

    suspend fun updateWord(gameId: Int, wordModel: GameWordModel)

    suspend fun createWord(value: String)

    suspend fun deleteGame(gameDBModel: GameDBModel)
}