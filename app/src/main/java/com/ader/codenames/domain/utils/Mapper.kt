package com.ader.codenames.domain.utils

import com.ader.codenames.data.model.GameWordDBModel
import com.ader.codenames.data.model.TeamDBModel
import com.ader.codenames.data.model.WordDBModel
import com.ader.codenames.data.model.embedded.GameEmbeddedModel
import com.ader.codenames.domain.model.GameModel
import com.ader.codenames.domain.model.GameWordModel
import com.ader.codenames.domain.model.TeamModel
import com.ader.codenames.domain.model.WordModel

object Mapper {
    fun getTeamByValue(value: Int): GameConstants.Team {
        return when(value){
            0 -> GameConstants.Team.FIRST_TEAM
            1 -> GameConstants.Team.SECOND_TEAM
            else -> GameConstants.Team.FIRST_TEAM
        }
    }

    fun getTeamColorByValue(value: Int): GameConstants.TeamColor {
        return when(value){
            0 -> GameConstants.TeamColor.RED
            1 -> GameConstants.TeamColor.BLUE
            else -> GameConstants.TeamColor.RED
        }
    }

    fun getWordTypeByValue(value: Int): GameConstants.GameWordType {
        return when(value){
            0 -> GameConstants.GameWordType.FIRST_TEAM
            1 -> GameConstants.GameWordType.SECOND_TEAM
            2 -> GameConstants.GameWordType.NEUTRAL
            3 -> GameConstants.GameWordType.KILLER
            else -> GameConstants.GameWordType.NEUTRAL
        }
    }

    fun mapTeamDBModelToTeamModel(teamDBModel: TeamDBModel): TeamModel {
        return TeamModel(
            id = teamDBModel.id,
            gameId = teamDBModel.gameId,
            teamColor = getTeamColorByValue(teamDBModel.teamColor),
            teamType = getTeamByValue(teamDBModel.orderNumber)
        )
    }

    fun mapWordDBModelToWordModel(wordDBModel: WordDBModel): WordModel {
        return WordModel(
            wordDBModel.id,
            wordDBModel.uaValue
        )
    }

    fun mapGameWordDBModelToGameWordModel(gameWordDBModel: GameWordDBModel): GameWordModel {
        return GameWordModel(
            WordModel(gameWordDBModel.wordId, gameWordDBModel.value),
            getWordTypeByValue(gameWordDBModel.type),
            gameWordDBModel.isOpen,
            gameWordDBModel.index
        )
    }

    fun mapGameWordModelToGameWordDBModel(gameID: Int, gameWordModel: GameWordModel): GameWordDBModel {
        return GameWordDBModel(
            gameWordModel.word.id,
            gameID,
            gameWordModel.type.value,
            gameWordModel.index,
            gameWordModel.isOpen,
            gameWordModel.word.value
        )
    }

    fun mapGameDBModelToGameModel(gameEmbeddedModel: GameEmbeddedModel): GameModel{
        return GameModel(
            gameEmbeddedModel.gameDBModel.id,
            gameEmbeddedModel.gameWords.map {
                mapGameWordDBModelToGameWordModel(it)
            }.sortedBy {
                       it.index
            },
            gameEmbeddedModel.gameDBModel.moveNumber,
            getTeamByValue(gameEmbeddedModel.gameDBModel.moveTeam),
            gameEmbeddedModel.teams.map {
                mapTeamDBModelToTeamModel(it)
            },
            gameEmbeddedModel.gameDBModel.masterKey
        )
    }
}