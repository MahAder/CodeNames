package com.ader.codenames.presentation.game

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ader.codenames.domain.interactor.game.GameInteractor
import com.ader.codenames.domain.interactor.game.GameManager
import com.ader.codenames.domain.interactor.game.model.GameStatus
import com.ader.codenames.domain.interactor.game.model.GameStatusModel
import com.ader.codenames.domain.utils.GameConstants
import com.ader.codenames.presentation.model.GameStatusUI
import com.ader.codenames.presentation.model.GameStatusUIModel
import com.ader.codenames.presentation.model.WordUIModel
import com.ader.eventbudget20.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val gameManager: GameManager,
    private val gameInteractor: GameInteractor
) : BaseViewModel() {
    val wordsLiveData = MutableLiveData<List<WordUIModel>>()

    val gameStatusLiveData = MutableLiveData<GameStatusUIModel>()

    val gameFinishedLiveData = MutableLiveData<Boolean>()

    var masterMode = false

    private val game = gameManager.openGame()

    init {
        initGame()
    }

    private fun initGame() {
        hideClosedCardsColor()
        updateGameStatusUI(gameManager.getGameStatus())
        if (gameManager.getGameStatus().moveCount == 0) {
            showAllCardsColor(game.masterKey)
        }
    }

    fun showAllCardsColor(masterKey: String) {
        if (game.masterKey != masterKey) return
        wordsLiveData.postValue(game.words.mapIndexed { index, gameWordModel ->
            openWord(index, false)
        })
        masterMode = true
    }

    fun hideClosedCardsColor() {
        wordsLiveData.postValue(game.words.mapIndexed { index, gameWordModel ->
            if (gameWordModel.isOpen) {
                openWord(index, false)
            } else {
                WordUIModel(
                    gameWordModel.word.value,
                    WordUIModel.WordType.CLOSED
                )
            }
        })
        masterMode = false
    }

    fun openWord(wordPosition: Int, changeOpenStatus: Boolean = true): WordUIModel {
        val word = game.words[wordPosition]
        val teams = game.teams.filter {
            it.teamType.value == word.type.value
        }
        val team = if (teams.isNotEmpty()) teams[0] else null
        val wordType = if (team != null) {
            getWordColorByTeamColor(team.teamColor)
        } else {
            getWordColorByWordType(word.type)
        }

        if (changeOpenStatus) {
            handleWordOpening(wordPosition)
        }
        val wordModel = WordUIModel(
            word.word.value,
            wordType
        )

        wordsLiveData.value?.let {
            (it as ArrayList<WordUIModel>).removeAt(wordPosition)
            it.add(wordPosition, wordModel)
        }

        return wordModel
    }

    fun finishGame(){
        viewModelScope.launch(Dispatchers.IO){
            val gameFinished = gameManager.finishGame()
            gameFinishedLiveData.postValue(gameFinished)
        }
    }

    fun endMove(){
        viewModelScope.launch(Dispatchers.IO){
            val gameStatus = gameManager.skipWord()
            updateGameStatusUI(gameStatus)
        }
    }

    private fun handleWordOpening(wordPosition: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val gameStatusModel = gameManager.openWord(wordPosition)
            updateGameStatusUI(gameStatusModel)
        }
    }

    private fun updateGameStatusUI(gameStatusModel: GameStatusModel) {
        val nextMoveTeam = game.teams.filter {
            it.teamType == gameStatusModel.nextMove
        }[0]

        val teamColor = nextMoveTeam.teamColor
        val firstTeam = game.teams.filter {
            it.teamType == GameConstants.Team.FIRST_TEAM
        }[0]
        val firstTeamColor = firstTeam.teamColor
        var blueWordsLeft: Int
        var redWordsLeft: Int
        if (firstTeamColor == GameConstants.TeamColor.RED) {
            redWordsLeft = gameStatusModel.firstTeamWordsLeftCount
            blueWordsLeft = gameStatusModel.secondTeamWordsLeftCount
        } else {
            blueWordsLeft = gameStatusModel.firstTeamWordsLeftCount
            redWordsLeft = gameStatusModel.secondTeamWordsLeftCount
        }
        val gameStatusUIModel = if (gameStatusModel.moveCount == 0) GameStatusUI.START_GAME else
            when (gameStatusModel.gameStatus) {
                GameStatus.NEXT_MOVE -> GameStatusUI.NEXT_MOVE
                GameStatus.WORD_CORRECT -> GameStatusUI.WORD_CORRECT
                GameStatus.FIRS_TEAM_WIN -> {
                    if (firstTeam.teamColor == GameConstants.TeamColor.RED) {
                        GameStatusUI.RED_TEAM_WIN
                    } else {
                        GameStatusUI.BLUE_TEAM_WIN
                    }
                }
                GameStatus.SECOND_TEAM_WIN -> {
                    if (firstTeam.teamColor == GameConstants.TeamColor.RED) {
                        GameStatusUI.BLUE_TEAM_WIN
                    } else {
                        GameStatusUI.RED_TEAM_WIN
                    }
                }
                GameStatus.DEATH_FIRST_TEAM_WIN -> {
                    if (firstTeam.teamColor == GameConstants.TeamColor.RED) {
                        GameStatusUI.DEATH_RED_TEAM_WIN
                    } else {
                        GameStatusUI.DEATH_BLUE_TEAM_WIN
                    }
                }
                GameStatus.DEATH_SECOND_TEAM_WIN -> {
                    if (firstTeam.teamColor == GameConstants.TeamColor.RED) {
                        GameStatusUI.DEATH_BLUE_TEAM_WIN
                    } else {
                        GameStatusUI.DEATH_RED_TEAM_WIN
                    }
                }
            }
        gameStatusLiveData.postValue(
            GameStatusUIModel(
                teamColor,
                gameStatusUIModel,
                gameStatusModel.moveCount,
                redWordsLeft,
                blueWordsLeft
            )
        )
    }

    private fun getWordColorByWordType(gameWordType: GameConstants.GameWordType): WordUIModel.WordType {
        return when (gameWordType) {
            GameConstants.GameWordType.NEUTRAL -> WordUIModel.WordType.YELLOW
            GameConstants.GameWordType.KILLER -> WordUIModel.WordType.BLACK
            else -> WordUIModel.WordType.YELLOW
        }
    }

    private fun getWordColorByTeamColor(teamColor: GameConstants.TeamColor): WordUIModel.WordType {
        return when (teamColor) {
            GameConstants.TeamColor.RED -> WordUIModel.WordType.RED
            GameConstants.TeamColor.BLUE -> WordUIModel.WordType.BLUE
        }
    }
}