package com.ader.codenames.domain.model

import com.ader.codenames.domain.utils.GameConstants

data class TeamModel(
    val id: Int,
    val gameId: Int,
    val teamType: GameConstants.Team,
    val teamColor: GameConstants.TeamColor
)