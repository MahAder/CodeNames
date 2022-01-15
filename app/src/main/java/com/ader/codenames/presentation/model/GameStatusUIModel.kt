package com.ader.codenames.presentation.model

import com.ader.codenames.domain.interactor.game.model.GameStatus
import com.ader.codenames.domain.utils.GameConstants

data class GameStatusUIModel(
    var nextMove: GameConstants.TeamColor,
    var gameStatus: GameStatusUI,
    var moveCount: Int,
    var redTeamWordsLeftCount: Int,
    var blueTeamWordsLeftCount: Int
)

enum class GameStatusUI {
    START_GAME, NEXT_MOVE, WORD_CORRECT, RED_TEAM_WIN, BLUE_TEAM_WIN, DEATH_RED_TEAM_WIN, DEATH_BLUE_TEAM_WIN
}