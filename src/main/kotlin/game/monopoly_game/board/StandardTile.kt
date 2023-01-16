package game.monopoly_game.board

import game.monopoly_game.MonopolyGame
import game.monopoly_game.MonopolyPlayer

class StandardTile(
    id: Int,
    name: String,
    override val price: Array<Int>,
    override val payout: Array<Int>
) : AbstractTile(id, name), Purchaseable {

    override val owner: MonopolyPlayer? = null
    override val level: Int = 0

    override fun onPlayerStep(player: MonopolyPlayer, gameState: MonopolyGame) {
        if (owner == null || player == owner) return

        val rentCost: Int = calculatePayout(gameState)
        player.deductMoney(rentCost)
        owner.addMoney(rentCost)
    }
}