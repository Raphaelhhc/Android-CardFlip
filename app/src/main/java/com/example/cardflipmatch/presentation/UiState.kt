package com.example.cardflipmatch.presentation

import com.example.cardflipmatch.module.Game

data class UiState(
    val turn: Int = 1,
    val game: Game = Game(),
)