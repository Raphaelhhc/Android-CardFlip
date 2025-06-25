package com.example.cardflipmatch.presentation

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cardflipmatch.module.BoardSize
import com.example.cardflipmatch.module.CardStatus
import com.example.cardflipmatch.module.GameLogic
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val gameLogic: GameLogic
): ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val firstFlippedCard = mutableStateOf<Pair<Int, Int>?>(null)
    private val secondFlippedCard = mutableStateOf<Pair<Int, Int>?>(null)

    init {
        Log.d("VM", "init ${_uiState.value}")
        createNewGame()
    }

    fun startNewGame() {
        createNewGame()
    }

    fun clickCard(row: Int, col: Int) {
        viewModelScope.launch {
            if (firstFlippedCard.value == null) {
                firstFlippedCard.value = Pair(row, col)
                flipCard(row, col)
                return@launch
            } else if (secondFlippedCard.value == null && row == firstFlippedCard.value!!.first && col == firstFlippedCard.value!!.second) {
                firstFlippedCard.value = null
                flipCard(row, col)
                return@launch
            } else if (secondFlippedCard.value == null) {
                secondFlippedCard.value = Pair(row, col)
                flipCard(row, col)
                eliminateCard()
                return@launch
            }
        }
    }

    private fun createNewGame() {
        val newGame = gameLogic.createNewGame(BoardSize.BOARD_4X4)
        Log.d("VM", "newGame $newGame")
        _uiState.value = UiState(
            turn = 1,
            game = newGame
        )
        Log.d("VM", "created ${_uiState.value}")
    }

    private suspend fun flipCard(row: Int, col: Int) {
        val newBoard = _uiState.value.game.board.copyOf()
        val card = newBoard[row][col]
        delay(200)
        newBoard[row][col] = when(card.status) {
            CardStatus.FRONT -> card.copy(status = CardStatus.BACK)
            CardStatus.BACK -> card.copy(status = CardStatus.FRONT)
            CardStatus.EMPTY -> error("Shall not be empty")
        }
    }

    private suspend fun eliminateCard() {
        val (row1, col1) = firstFlippedCard.value!!
        val (row2, col2) = secondFlippedCard.value!!
        val newBoard = _uiState.value.game.board.copyOf()
        if (newBoard[row1][col1].cardImage == newBoard[row2][col2].cardImage) {
            delay(500)
            newBoard[row1][col1] = newBoard[row1][col1].copy(status = CardStatus.EMPTY)
            newBoard[row2][col2] = newBoard[row2][col2].copy(status = CardStatus.EMPTY)
            val newGame = _uiState.value.game.copy(board = newBoard)
            _uiState.value = _uiState.value.copy(game = newGame)
        }
    }

}