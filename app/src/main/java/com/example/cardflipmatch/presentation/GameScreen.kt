package com.example.cardflipmatch.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.example.cardflipmatch.module.CardStatus
import com.example.cardflipmatch.module.Game
import com.example.cardflipmatch.module.GameCard

@Composable
fun GameScreen(
    modifier: Modifier,
    vm: GameViewModel
) {
    val uiState by vm.uiState.collectAsState()
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val configuration = LocalConfiguration.current
        // Title
        Text("Card Flip Game")

        // Turn
        Text("Turn: ${uiState.turn}")

        // Board
        if (uiState.game.board.isEmpty()) {
            Text("Wait for initializing...")
        } else {
            BoardArea(
                modifier = Modifier.fillMaxWidth().height(configuration.screenWidthDp.dp),
                game = uiState.game,
                onClickCard = { row, col ->
                    vm.clickCard(row, col)
                }
            )
        }

        // Button
        Button(
            onClick = {
                vm.startNewGame()
            }
        ) {
            Text("New Game")
        }
    }
}

@Composable
fun BoardArea(
    modifier: Modifier,
    game: Game,
    onClickCard: (Int, Int) -> Unit
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            for (row in 0 until game.boardSize.side) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (col in 0 until game.boardSize.side) {
                        GameCardSlot(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(1f),
                            gameCard = game.board[row][col],
                            onClickCard = {
                                onClickCard(row, col)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GameCardSlot(
    modifier: Modifier,
    gameCard: GameCard,
    onClickCard: () -> Unit
) {
    Box(
        modifier = modifier
            .clickable {
                onClickCard()
            },
        contentAlignment = Alignment.Center
    ) {
        when (gameCard.status) {
            CardStatus.EMPTY -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .background(
                            color = Color.Transparent,
                            shape = RoundedCornerShape(percent = 10)
                        )
                )
            }

            CardStatus.BACK -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .background(
                            color = Color.Blue,
                            shape = RoundedCornerShape(percent = 10)
                        )
                )
            }

            CardStatus.FRONT -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .border(
                            width = 4.dp,
                            color = Color.Gray
                        )
                        .background(
                            color = Color.LightGray,
                            shape = RoundedCornerShape(percent = 10)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        imageVector = gameCard.cardImage.image,
                        contentDescription = gameCard.cardImage.name
                    )
                }
            }
        }
    }
}
