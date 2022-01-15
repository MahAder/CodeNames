package com.ader.codenames.domain.interactor.game

import com.ader.codenames.domain.interactor.game.model.GameStatus
import com.ader.codenames.domain.interactor.game.model.GameStatusModel
import com.ader.codenames.domain.model.GameModel

interface GameManager {
    suspend fun newGame(masterKey: String): Boolean

    suspend fun checkIfGameSaved(): Boolean

    fun openGame(): GameModel

    suspend fun openWord(index: Int): GameStatusModel

    suspend fun skipWord(): GameStatusModel

    fun getGameStatus(): GameStatusModel

    suspend fun finishGame(): Boolean
}