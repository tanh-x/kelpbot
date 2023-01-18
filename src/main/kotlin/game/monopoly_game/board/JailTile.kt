package game.monopoly_game.board

import game.monopoly_game.MonopolyGame
import game.monopoly_game.MonopolyPlayer

class JailTile(
    id: Int,
    name: String,
) : AbstractTile(id, name) {
    override fun onPlayerStep(player: MonopolyPlayer, gameState: MonopolyGame): Unit {}
}