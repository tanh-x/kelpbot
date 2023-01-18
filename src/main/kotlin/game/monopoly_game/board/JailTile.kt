package game.monopoly_game.board

import game.monopoly_game.MonopolyGame
import game.monopoly_game.MonopolyPlayer

class JailTile(
    position: Int,
    name: String,
) : AbstractTile(position, name) {
    override fun onPlayerStep(player: MonopolyPlayer, gameState: MonopolyGame): Unit {}
}