package game.monopoly_game.board

import game.monopoly_game.data.BoardLookupTable


/**
 * Data class representing a Monopoly board, holds the list of [AbstractTile]s as
 * they update throughout the game, as well as several methods aiding the functionality
 * of the game.
 *
 * @param tileset Array of [AbstractTile] to start with.
 * @param boardLUT [BoardLookupTable] instance, allows us to use a simple and
 * more efficient hard-coded implementation.
 */
data class MonopolyBoard(
    val tileset: Array<AbstractTile>,
    val boardLUT: BoardLookupTable
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as MonopolyBoard
        if (!tileset.contentEquals(other.tileset)) return false
        return true
    }

    override fun hashCode(): Int = tileset.contentHashCode()
}