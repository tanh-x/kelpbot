package game.monopoly_game.board.tile

import game.monopoly_game.MonopolyGame
import game.monopoly_game.MonopolyPlayer

class JailTile(
    id: Int,
    name: String,
    shorthand: String,
) : AbstractTile(id, name, shorthand) {
    override fun onPlayerStep(player: MonopolyPlayer, gameState: MonopolyGame): Unit {}
}