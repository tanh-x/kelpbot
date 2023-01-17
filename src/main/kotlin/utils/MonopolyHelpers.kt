package utils

import game.monopoly_game.board.MonopolyBoard
import game.monopoly_game.data.BoardLookupTable

fun instantiateStandardBoard(): MonopolyBoard {
    return MonopolyBoard(
        emptyArray(),
        BoardLookupTable()
    )
}