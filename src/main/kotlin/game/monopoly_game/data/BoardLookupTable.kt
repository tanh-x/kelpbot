package game.monopoly_game.data

/**
 * Data class storing some convenient values corresponding to a
 * [game.monopoly_game.board.MonopolyBoard] that would otherwise need to be
 * repeatedly computed at runtime.
 */
data class BoardLookupTable(
    val utilities: Array<Int> = MonopolyConstants.STD_UTILITY_POSITIONS,
    val railroads: Array<Int> = MonopolyConstants.STD_RAILROAD_POSITIONS,
    val boardSize: Int = MonopolyConstants.STD_BOARD_SIZE,
)