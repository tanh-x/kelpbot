package game.monopoly_game.board

import game.monopoly_game.data.TileColor

/**
 * Data class representing a Monopoly board, holds the list of [AbstractTile]s as
 * they update throughout the game, as well as several methods aiding the functionality
 * of the game.
 *
 * @param tileset Array of [AbstractTile] to start with.
 */
data class MonopolyBoard(
    val tileset: Array<AbstractTile>,
    val colorSets: Map<TileColor, Array<Int>>
) {
    val size: Int = tileset.size

    private val utilityIdxs: Array<Int> = tileset.mapIndexedNotNull { idx: Int, tile: AbstractTile ->
        idx.takeIf { tile is UtilityTile }
    }.toTypedArray()

    private val railroadIdxs: Array<Int> = tileset.mapIndexedNotNull { idx: Int, tile: AbstractTile ->
        idx.takeIf { tile is RailroadTile }
    }.toTypedArray()

    val jailIdx: Int = tileset.mapIndexedNotNull { idx: Int, tile: AbstractTile ->
        idx.takeIf { tile is JailTile }
    }.apply {
        if (size != 1) throw IllegalStateException("Found $size jail tiles in tileset ($this)")
    }.first()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as MonopolyBoard
        if (!tileset.contentEquals(other.tileset)) return false
        return true
    }

    override fun hashCode(): Int = tileset.contentHashCode()
}