package com.ader.codenames.domain.interactor.game.model

import com.ader.codenames.domain.utils.GameConstants

data class GameStatusModel(
    var nextMove: GameConstants.Team,
    var gameStatus: GameStatus,
    var moveCount: Int,
    var firstTeamWordsLeftCount: Int,
    var secondTeamWordsLeftCount: Int
)

enum class GameStatus {
    NEXT_MOVE, WORD_CORRECT, FIRS_TEAM_WIN, SECOND_TEAM_WIN, DEATH_FIRST_TEAM_WIN, DEATH_SECOND_TEAM_WIN
}