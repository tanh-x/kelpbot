package game.monopoly_game.board

import game.monopoly_game.MonopolyGame
import game.monopoly_game.MonopolyPlayer
import java.lang.IllegalStateException

interface Purchaseable {
    val price: Array<Int>
    val owner: MonopolyPlayer?
    val level: Int
    val payout: Array<Int>

    fun calculatePayout(gameState: MonopolyGame): Int {
        return payout.getOrElse(level) {
            throw IllegalStateException("Invalid property level $level on ${(this as? AbstractTile)?.name}")
        }
    }
}