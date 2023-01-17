package game.monopoly_game.board

import game.monopoly_game.MonopolyGame
import game.monopoly_game.MonopolyPlayer
import game.monopoly_game.data.TileColors

class StandardTile(
    id: Int,
    name: String,
    val color: TileColors,
    override val price: Array<Int>,
    override val payout: Array<Int>
) : AbstractTile(id, name), Purchasable {

    override val owner: MonopolyPlayer? = null
    override val level: Int = 0

    override fun onPlayerStep(player: MonopolyPlayer, gameState: MonopolyGame) {
        if (owner == null || player == owner) return

        val rentCost: Int = calculatePayout(gameState)
        player.deductMoney(rentCost)
        owner.addMoney(rentCost)
    }
}