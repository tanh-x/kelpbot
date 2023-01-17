package utils

import game.monopoly_game.board.MonopolyBoard

fun instantiateStandardBoard(): MonopolyBoard {
    return MonopolyBoard(
        emptyArray(),
    )
}