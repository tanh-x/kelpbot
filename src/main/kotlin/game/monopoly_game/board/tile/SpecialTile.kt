package game.monopoly_game.board.tile

import game.monopoly_game.MonopolyGame
import game.monopoly_game.MonopolyPlayer

class SpecialTile(
    position: Int,
    name: String,
    shorthand: String,
    private inline val effect: (MonopolyPlayer, MonopolyGame) -> Unit
) : AbstractTile(position, name, shorthand) {
    override fun onPlayerStep(player: MonopolyPlayer, gameState: MonopolyGame): Unit = effect(player, gameState)
}