package com.example.cardflipmatch.presentation

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cardflipmatch.module.BoardSize
import com.example.cardflipmatch.module.CardStatus
import com.example.cardflipmatch.module.GameLogic
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
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
                eliminateOrTurnBackCard()
                return@launch
            }
        }
    }

    private fun createNewGame() {
        val newGame = gameLogic.createNewGame(BoardSize.BOARD_4X4)
        _uiState.value = UiState(
            turn = 1,
            game = newGame
        )
    }

    private suspend fun flipCard(row: Int, col: Int) {
        val oldBoard = _uiState.value.game.board
        val newBoard = Array(oldBoard.size) { r ->
            oldBoard[r].clone()
        }
        val card = newBoard[row][col]
        delay(200)
        newBoard[row][col] = when(card.status) {
            CardStatus.FRONT -> card.copy(status = CardStatus.BACK)
            CardStatus.BACK -> card.copy(status = CardStatus.FRONT)
            CardStatus.EMPTY -> error("Shall not be empty")
        }
        val newGame = _uiState.value.game.copy(board = newBoard)
        _uiState.value = _uiState.value.copy(game = newGame)
    }

    private suspend fun eliminateOrTurnBackCard() {
        val (row1, col1) = firstFlippedCard.value!!
        val (row2, col2) = secondFlippedCard.value!!
        val oldBoard = _uiState.value.game.board
        val newBoard = Array(oldBoard.size) { r ->
            oldBoard[r].clone()
        }
        if (newBoard[row1][col1].cardImage == newBoard[row2][col2].cardImage) {
            delay(500)
            newBoard[row1][col1] = newBoard[row1][col1].copy(status = CardStatus.EMPTY)
            newBoard[row2][col2] = newBoard[row2][col2].copy(status = CardStatus.EMPTY)
            Toast.makeText(context, "Match!", Toast.LENGTH_SHORT).show()
        } else {
            delay(700)
            newBoard[row1][col1] = newBoard[row1][col1].copy(status = CardStatus.BACK)
            newBoard[row2][col2] = newBoard[row2][col2].copy(status = CardStatus.BACK)
            Toast.makeText(context, "Not match!", Toast.LENGTH_SHORT).show()
        }
        firstFlippedCard.value = null
        secondFlippedCard.value = null
        val newGame = _uiState.value.game.copy(board = newBoard)
        val nextTurn = _uiState.value.turn + 1
        _uiState.value = _uiState.value.copy(turn = nextTurn, game = newGame)
    }

}