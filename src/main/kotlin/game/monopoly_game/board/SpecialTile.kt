package game.monopoly_game.board

import game.monopoly_game.MonopolyGame
import game.monopoly_game.MonopolyPlayer

class SpecialTile(
id: Int,
    name: String,
    private inline val effect: (MonopolyPlayer, MonopolyGame) -> Unit
) : AbstractTile(id, name) {
    override fun onPlayerStep(player: MonopolyPlayer, gameState: MonopolyGame): Unit = effect(player, gameState)
}