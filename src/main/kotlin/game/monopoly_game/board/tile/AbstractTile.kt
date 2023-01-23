package game.monopoly_game.board.tile

import game.monopoly_game.MonopolyGame
import game.monopoly_game.MonopolyPlayer

abstract class AbstractTile(
    val position: Int,
    val name: String,
    val shorthand: String,
) {
    abstract fun onPlayerStep(player: MonopolyPlayer, gameState: MonopolyGame): Unit
}