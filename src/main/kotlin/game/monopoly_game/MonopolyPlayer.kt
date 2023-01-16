package game.monopoly_game

import dev.kord.core.entity.Member
import game.AbstractPlayer
import game.monopoly_game.board.Purchaseable
import game.monopoly_game.data.MonopolyConstants
import java.lang.IllegalArgumentException

class MonopolyPlayer(
    member: Member,
    isHost: Boolean = false,
) : AbstractPlayer(member, isHost) {
    var money: Int = MonopolyConstants.STARTING_BALANCE
        private set
    val position: Int = 0
    val owns: MutableMap<Int, Purchaseable> = mutableMapOf()

    fun deductMoney(n: Int): Unit {
        if (n < 0) throw IllegalArgumentException("Tried to deduct $name a negative amount of money: $n")
        money -= n
    }

    fun addMoney(n: Int): Unit {
        if (n < 0) throw IllegalArgumentException("Tried to give $name a negative amount of money: $n")
    }
}