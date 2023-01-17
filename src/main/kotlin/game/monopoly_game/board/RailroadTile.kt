package game.monopoly_game.board

import game.monopoly_game.MonopolyGame
import game.monopoly_game.MonopolyPlayer

class RailroadTile(
    id: Int,
    name: String,
    override val price: Array<Int>,
    override val payout: Array<Int>
) : AbstractTile(id, name), Purchasable {

    override val owner: MonopolyPlayer? = null
    override val level: Int
        get() = 0

    override fun onPlayerStep(player: MonopolyPlayer, gameState: MonopolyGame): Unit {

    }
}