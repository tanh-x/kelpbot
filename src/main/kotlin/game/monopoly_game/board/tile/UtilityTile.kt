package game.monopoly_game.board.tile

import game.monopoly_game.MonopolyGame
import game.monopoly_game.MonopolyPlayer

class UtilityTile(
    id: Int,
    name: String,
    shorthand: String,
    override val price: Int,
    override val payout: Array<Int>
) : AbstractTile(id, name, shorthand), Purchasable {

    override var owner: MonopolyPlayer? = null
    override val level: Int
        get() = 0

    override fun onPlayerStep(player: MonopolyPlayer, gameState: MonopolyGame): Unit {

    }
}