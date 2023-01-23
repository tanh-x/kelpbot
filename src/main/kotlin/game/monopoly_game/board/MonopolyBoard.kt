package game.monopoly_game.board

import game.monopoly_game.board.tile.AbstractTile
import game.monopoly_game.board.tile.JailTile
import game.monopoly_game.board.tile.RailroadTile
import game.monopoly_game.board.tile.UtilityTile
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
    val colorSets: Map<TileColor, Array<Int>>,
    private val shorthands: Map<String, Int>
) {
    val size: Int = tileset.size

//    private val utilityIdxs: Array<Int> = tileset.mapIndexedNotNull { idx: Int, tile: AbstractTile ->
//        idx.takeIf { tile is UtilityTile }
//    }.toTypedArray()
//
//    private val railroadIdxs: Array<Int> = tileset.mapIndexedNotNull { idx: Int, tile: AbstractTile ->
//        idx.takeIf { tile is RailroadTile }
//    }.toTypedArray()
//
//    val jailIdx: Int = tileset.mapIndexedNotNull { idx: Int, tile: AbstractTile ->
//        idx.takeIf { tile is JailTile }
//    }.apply {
//        if (size != 1) throw IllegalStateException("Found $size jail tiles in tileset ($this)")
//    }.first()

    private val utilityIdx: Array<Int>
    private val railroadIdx: Array<Int>
    val jailIdx: Int

    init {
        val utility: ArrayList<Int> = arrayListOf()
        val railroad: ArrayList<Int> = arrayListOf()
        val jail: ArrayList<Int> = arrayListOf()

        tileset.forEachIndexed { index: Int, tile: AbstractTile ->
            when (tile) {
                is UtilityTile -> utility
                is RailroadTile -> railroad
                is JailTile -> jail
                else -> null
            }?.add(index)
        }

        if (jail.size != 1) throw IllegalStateException("Found $size jail tiles in tileset ($this)")

        utilityIdx = utility.toTypedArray()
        railroadIdx = railroad.toTypedArray()
        jailIdx = jail[0]
    }

    fun getTileFromShorthand(s: String): AbstractTile? = shorthands[s]?.let { tileset[it] }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as MonopolyBoard
        if (!tileset.contentEquals(other.tileset)) return false
        return true
    }

    override fun hashCode(): Int = tileset.contentHashCode()
}