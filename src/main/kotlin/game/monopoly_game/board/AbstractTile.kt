package game.monopoly_game.board

import game.monopoly_game.MonopolyGame
import game.monopoly_game.MonopolyPlayer

abstract class AbstractTile(
    val position: Int,
    val name: String,
) {
    abstract fun onPlayerStep(player: MonopolyPlayer, gameState: MonopolyGame): Unit
}