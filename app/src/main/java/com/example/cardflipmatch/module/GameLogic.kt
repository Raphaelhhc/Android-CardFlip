package com.example.cardflipmatch.module

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.ui.graphics.vector.ImageVector
import javax.inject.Inject

class GameLogic @Inject constructor() {

    private val cardTypes = listOf(
        GameCard(CardStatus.BACK, CardImage.CHECK),
        GameCard(CardStatus.BACK, CardImage.STAR),
        GameCard(CardStatus.BACK, CardImage.FACE),
        GameCard(CardStatus.BACK, CardImage.HOME),
        GameCard(CardStatus.BACK, CardImage.HEART),
        GameCard(CardStatus.BACK, CardImage.PERSON),
        GameCard(CardStatus.BACK, CardImage.THUMB),
        GameCard(CardStatus.BACK, CardImage.PLACE),
    )

    fun createNewGame(boardSize: BoardSize): Game {
        val side = boardSize.side
        val cards = (cardTypes + cardTypes).shuffled()
        val temp = Array(side) { Array<GameCard?>(side) { null } }
        for (index in 0 until side * side) {
            val row = index / side
            val col = index % side
            temp[row][col] = cards[index]
        }
        val newBoard: Array<Array<GameCard>> = Array(side) { row ->
            Array(side) { col ->
                temp[row][col] ?: error("board slot [$row][$col] not initialized")
            }
        }
        return Game(
            boardSize = boardSize,
            board = newBoard
        )
    }

}

data class Game(
    val boardSize: BoardSize = BoardSize.BOARD_4X4,
    val board: Array<Array<GameCard>> = emptyArray()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Game) return false
        return boardSize == other.boardSize &&
                board.contentDeepEquals(other.board)
    }

    override fun hashCode(): Int =
        31 * boardSize.hashCode() + board.contentDeepHashCode()
}

data class GameCard(
    val status: CardStatus,
    val cardImage: CardImage
)

enum class CardStatus {
    EMPTY, FRONT, BACK
}

enum class BoardSize(val side: Int) {
    BOARD_4X4(4)
}

enum class CardImage(val image: ImageVector) {
    STAR(Icons.Default.Star),
    CHECK(Icons.Default.Check),
    FACE(Icons.Default.Face),
    HEART(Icons.Default.Favorite),
    HOME(Icons.Default.Home),
    PERSON(Icons.Default.Person),
    THUMB(Icons.Default.ThumbUp),
    PLACE(Icons.Default.Place)
}