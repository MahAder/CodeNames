package com.ader.codenames.domain.interactor.game

import com.ader.codenames.data.dao.GameDao
import com.ader.codenames.data.dao.GameWordDao
import com.ader.codenames.data.dao.TeamDao
import com.ader.codenames.data.dao.WordDao
import com.ader.codenames.data.model.GameDBModel
import com.ader.codenames.data.model.GameWordDBModel
import com.ader.codenames.data.model.TeamDBModel
import com.ader.codenames.data.model.WordDBModel
import com.ader.codenames.domain.model.GameModel
import com.ader.codenames.domain.model.GameWordModel
import com.ader.codenames.domain.model.TeamModel
import com.ader.codenames.domain.model.WordModel
import com.ader.codenames.domain.utils.GameConstants
import com.ader.codenames.domain.utils.GameUtils
import com.ader.codenames.domain.utils.Mapper
import java.util.ArrayList
import javax.inject.Inject

class GameInteractorImpl @Inject constructor(
    private val gameDao: GameDao,
    private val teamDao: TeamDao,
    private val gameWordDao: GameWordDao,
    private val wordsDao: WordDao
) : GameInteractor {
    override suspend fun createNewGame(masterKey: String): GameModel {
        return createGame(masterKey)
    }

    override suspend fun getActiveGame(): GameModel? {
        val activeGame = gameDao.getActiveGame() ?: return null
        return Mapper.mapGameDBModelToGameModel(activeGame)
    }

    override suspend fun updateGame(gameDBModel: GameDBModel) {
        gameDao.update(gameDBModel)
    }

    override suspend fun updateWord(gameId: Int, wordModel: GameWordModel) {
        gameWordDao.update(Mapper.mapGameWordModelToGameWordDBModel(gameId, wordModel))
    }

    override suspend fun createWord(value: String) {
        wordsDao.insert(WordDBModel(uaValue = value))
    }

    override suspend fun deleteGame(gameDBModel: GameDBModel) {
        gameDao.delete(gameDBModel)
    }

    private suspend fun generateGameWords(gameId: Int): List<GameWordModel> {
        val words = wordsDao.getRandomWords(GameConstants.DEFAULT_WORDS_COUNT)
            .map {
                WordModel(
                    it.id,
                    it.uaValue
                )
            }

        val leftWordsList = ArrayList(words)
        val gameWordList = ArrayList<GameWordModel>()
        var leftWordsMaxIndex = GameConstants.DEFAULT_WORDS_COUNT - 1

        val killerWordIndex = GameUtils.random(leftWordsMaxIndex)
        val killerWord = words[killerWordIndex]
        leftWordsList.remove(killerWord)
        leftWordsMaxIndex -= 1
        gameWordList.add(
            GameWordModel(
                killerWord,
                GameConstants.GameWordType.KILLER,
                false,
                killerWordIndex
            )
        )

        for (i in 0 until GameConstants.DEFAULT_FIRST_TEAM_WORDS_COUNT + GameConstants.DEFAULT_SECOND_TEAM_WORDS_COUNT) {
            val word = leftWordsList[GameUtils.random(leftWordsMaxIndex)]
            val wordIndex = words.indexOf(word)
            val wordType = if (i % 2 == 0) {
                GameConstants.GameWordType.FIRST_TEAM
            } else {
                GameConstants.GameWordType.SECOND_TEAM
            }

            leftWordsList.remove(word)
            leftWordsMaxIndex -= 1
            gameWordList.add(GameWordModel(word, wordType, false, wordIndex))
        }

        for (word in leftWordsList) {
            val index = words.indexOf(word)
            gameWordList.add(GameWordModel(word, GameConstants.GameWordType.NEUTRAL, false, index))
        }

        for (word in gameWordList) {
            createGameWord(word.word, gameId, word.type, word.index)
        }

        return gameWordList.sortedBy {
            it.index
        }
    }

    private suspend fun createGameWord(
        wordModel: WordModel,
        gameId: Int,
        type: GameConstants.GameWordType,
        index: Int
    ) {
        val gameWordDBModel = GameWordDBModel(
            wordId = wordModel.id,
            gameId = gameId,
            type = type.value,
            index = index,
            value = wordModel.value
        )

        gameWordDao.insert(gameWordDBModel)
    }

    private suspend fun createGame(masterKey: String): GameModel {
        val gameDBModel = GameDBModel(
            id = GameConstants.DEFAULT_GAME_ID,
            moveNumber = 0,
            moveTeam = 0,
            masterKey = masterKey
        )

        gameDao.insert(gameDBModel)

        val firstTeamModel = createFirstTeam(GameConstants.DEFAULT_GAME_ID)
        val secondTeamColor = if(firstTeamModel.teamColor == GameConstants.TeamColor.BLUE){
            GameConstants.TeamColor.RED
        } else {
            GameConstants.TeamColor.BLUE
        }
        val secondTeam = createSecondTeam(GameConstants.DEFAULT_GAME_ID, secondTeamColor)

        val words = generateGameWords(GameConstants.DEFAULT_GAME_ID)

        return GameModel(
            GameConstants.DEFAULT_GAME_ID,
            words,
            0,
            GameConstants.Team.FIRST_TEAM,
            arrayListOf(firstTeamModel, secondTeam),
            masterKey
        )
    }

    private suspend fun createFirstTeam(gameId: Int): TeamModel {
        val teamColor = Mapper.getTeamColorByValue(GameUtils.random(1))
        val teamType = GameConstants.Team.FIRST_TEAM

        return createTeam(gameId, teamType, teamColor)
    }

    private suspend fun createSecondTeam(
        gameId: Int,
        teamColor: GameConstants.TeamColor
    ): TeamModel {
        return createTeam(gameId, GameConstants.Team.SECOND_TEAM, teamColor)
    }

    private suspend fun createTeam(
        gameId: Int,
        team: GameConstants.Team,
        teamColor: GameConstants.TeamColor
    ): TeamModel {
        val teamDBModel = TeamDBModel(
            gameId = gameId,
            orderNumber = team.value,
            teamColor = teamColor.value
        )

        teamDao.insert(teamDBModel)

        return Mapper.mapTeamDBModelToTeamModel(teamDBModel)
    }
}