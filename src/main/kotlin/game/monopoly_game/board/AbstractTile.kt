package game.monopoly_game.board

import game.monopoly_game.MonopolyGame
import game.monopoly_game.MonopolyPlayer

abstract class AbstractTile(
    val id: Int,
    val name: String,
) {
    abstract fun onPlayerStep(p: MonopolyPlayer, gameState: MonopolyGame): Unit
}