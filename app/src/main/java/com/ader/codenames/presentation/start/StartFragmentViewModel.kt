package com.ader.codenames.presentation.start

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ader.codenames.domain.interactor.game.GameManager
import com.ader.eventbudget20.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StartFragmentViewModel @Inject constructor(private val gameManager: GameManager): BaseViewModel() {
    val openGameLiveData = MutableLiveData<Boolean>()
    val isGameSaved = MutableLiveData<Boolean>()
    val loadingLiveData = MutableLiveData<Boolean>()

    fun loadInfo(){
        loadGame()
    }

    private fun loadGame(){
        viewModelScope.launch(Dispatchers.IO){
            isGameSaved.postValue(gameManager.checkIfGameSaved())
        }
    }

    fun startNewGame(masterKey: String){
        loadingLiveData.postValue(true)
        viewModelScope.launch(Dispatchers.IO){
            val game = gameManager.newGame(masterKey)
            loadingLiveData.postValue(false)
            openGameLiveData.postValue(game)
        }
    }

    fun loadSavedGame(){
        if(isGameSaved.value == true){
            openGameLiveData.postValue(true)
        }
    }

    fun redirectionComplete(){
        openGameLiveData.postValue(false)
    }
}