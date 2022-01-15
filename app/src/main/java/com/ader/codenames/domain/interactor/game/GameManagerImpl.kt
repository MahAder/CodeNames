package com.ader.codenames.domain.interactor.game

import android.content.Context
import com.ader.codenames.data.model.GameDBModel
import com.ader.codenames.data.preferences.PreferenceRepository
import com.ader.codenames.domain.interactor.game.model.GameStatus
import com.ader.codenames.domain.interactor.game.model.GameStatusModel
import com.ader.codenames.domain.model.GameModel
import com.ader.codenames.domain.model.GameWordModel
import com.ader.codenames.domain.utils.GameConstants
import com.ader.codenames.domain.utils.Mapper
import org.json.JSONArray
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset
import javax.inject.Inject

class GameManagerImpl @Inject constructor(
    private val gameInteractor: GameInteractor,
    private val context: Context,
    private val preferenceRepository: PreferenceRepository
) : GameManager {
    private lateinit var game: GameModel

    private lateinit var moveTeam: GameConstants.Team

    private lateinit var gameStatusModel: GameStatusModel

    override suspend fun newGame(masterKey: String): Boolean {
        createWords()
        this.game = gameInteractor.createNewGame(masterKey)
        initGameStatus()
        return true
    }

    override suspend fun checkIfGameSaved(): Boolean {
        val savedGame = gameInteractor.getActiveGame()
        if (savedGame != null) {
            this.game = savedGame
            initGameStatus()
        }

        return savedGame != null
    }

    override fun openGame(): GameModel {
        return game
    }

    private fun initGameStatus() {
        moveTeam = game.moveTeam
        gameStatusModel = GameStatusModel(
            moveTeam,
            GameStatus.NEXT_MOVE,
            game.moveNumber,
            calculateFirstTeamWordsCount(),
            calculateSecondTeamWordsCount()
        )
    }

    private fun calculateFirstTeamWordsCount(): Int {
        val openWords = game.words.filter {
            it.type == GameConstants.GameWordType.FIRST_TEAM
        }.filter {
            it.isOpen
        }.size

        return GameConstants.DEFAULT_FIRST_TEAM_WORDS_COUNT - openWords
    }

    private fun calculateSecondTeamWordsCount(): Int {
        val openWords = game.words.filter {
            it.type == GameConstants.GameWordType.SECOND_TEAM
        }.filter {
            it.isOpen
        }.size

        return GameConstants.DEFAULT_SECOND_TEAM_WORDS_COUNT - openWords
    }

    override suspend fun skipWord(): GameStatusModel {
        moveTeam = nextTeam()
        val gameStatus = GameStatus.NEXT_MOVE
        gameStatusModel.gameStatus = gameStatus
        gameStatusModel.nextMove = moveTeam

        gameInteractor.updateGame(
            GameDBModel(
                GameConstants.DEFAULT_GAME_ID,
                gameStatusModel.moveCount,
                moveTeam.value,
                game.masterKey
            )
        )

        return gameStatusModel
    }

    override suspend fun openWord(index: Int): GameStatusModel {
        val word = game.words[index]

        gameStatusModel.moveCount += 1

        word.isOpen = true
        updateWord(word)

        var gameStatus = GameStatus.WORD_CORRECT
        val isWordCorrect = word.type.value == moveTeam.value
        if (!isWordCorrect) {
            if (word.type == GameConstants.GameWordType.KILLER) {
                gameStatus = completeDeathGameStatus()
                moveTeam = nextTeam()
            } else {
                moveTeam = nextTeam()
                gameStatus = GameStatus.NEXT_MOVE
                if (word.type == GameConstants.GameWordType.FIRST_TEAM) {
                    gameStatusModel.firstTeamWordsLeftCount -= 1
                }

                if (word.type == GameConstants.GameWordType.SECOND_TEAM) {
                    gameStatusModel.secondTeamWordsLeftCount -= 1
                }
            }
        } else {
            if (word.type == GameConstants.GameWordType.FIRST_TEAM) {
                gameStatusModel.firstTeamWordsLeftCount -= 1
            }

            if (word.type == GameConstants.GameWordType.SECOND_TEAM) {
                gameStatusModel.secondTeamWordsLeftCount -= 1
            }
        }

        if (gameStatusModel.firstTeamWordsLeftCount == 0) {
            gameStatus = GameStatus.FIRS_TEAM_WIN
        }

        if (gameStatusModel.secondTeamWordsLeftCount == 0) {
            gameStatus = GameStatus.SECOND_TEAM_WIN
        }

        gameStatusModel.nextMove = moveTeam
        gameStatusModel.gameStatus = gameStatus

        gameInteractor.updateGame(
            GameDBModel(
                GameConstants.DEFAULT_GAME_ID,
                gameStatusModel.moveCount,
                moveTeam.value,
                game.masterKey
            )
        )

        return gameStatusModel
    }

    private fun completeDeathGameStatus(): GameStatus {
        return when (moveTeam) {
            GameConstants.Team.FIRST_TEAM -> GameStatus.DEATH_SECOND_TEAM_WIN
            GameConstants.Team.SECOND_TEAM -> GameStatus.DEATH_FIRST_TEAM_WIN
        }
    }

    private fun nextTeam(): GameConstants.Team {
        return when (moveTeam) {
            GameConstants.Team.FIRST_TEAM -> GameConstants.Team.SECOND_TEAM
            GameConstants.Team.SECOND_TEAM -> GameConstants.Team.FIRST_TEAM
        }
    }

    private suspend fun updateWord(gameWordModel: GameWordModel) {
        gameInteractor.updateWord(game.id, gameWordModel)
    }

    private suspend fun createWords() {
        if(!preferenceRepository.isWordsMigratedToDB()) {
            var json: String? = null
            json = try {
                val inputStream: InputStream = context.assets.open("words_ua.json")
                val size: Int = inputStream.available()
                val buffer = ByteArray(size)
                inputStream.read(buffer)
                inputStream.close()
                String(buffer, Charset.forName("UTF-8"))
            } catch (ex: IOException) {
                ex.printStackTrace()
                ""
            }

            val wordsArray = JSONArray(json)
            for (i in 0 until wordsArray.length()) {
                val word = wordsArray.getString(i)
                gameInteractor.createWord(word)
            }

            preferenceRepository.saveWordsMigratedToDB()
        }
    }

    override fun getGameStatus(): GameStatusModel {
        return gameStatusModel
    }

    override suspend fun finishGame(): Boolean {
        gameInteractor.deleteGame(
            GameDBModel(
                GameConstants.DEFAULT_GAME_ID,
                gameStatusModel.moveCount,
                moveTeam.value,
                game.masterKey
            )
        )
        return true
    }
}