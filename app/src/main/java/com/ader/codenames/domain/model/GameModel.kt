package com.ader.codenames.domain.model

import com.ader.codenames.domain.utils.GameConstants

data class GameModel(
    val id: Int,
    val words: List<GameWordModel>,
    val moveNumber: Int,
    val moveTeam: GameConstants.Team,
    val teams: List<TeamModel>,
    val masterKey: String
)