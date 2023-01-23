package game.monopoly_game

import dev.kord.core.entity.Member
import game.AbstractPlayer
import game.monopoly_game.board.tile.Purchasable
import game.monopoly_game.data.MonopolyGameplayParams
import java.lang.IllegalArgumentException

class MonopolyPlayer(
    member: Member,
    isHost: Boolean = false,
    private val gameParams: MonopolyGameplayParams
) : AbstractPlayer(member, isHost) {
    var money: Int = gameParams.startingBalance
        private set
    var position: Int = 0
    val owns: MutableMap<Int, Purchasable> = mutableMapOf()

    var jailTurns: Int = -1
    var isBankrupt: Boolean = false

    fun deductMoney(n: Int): Unit {
        if (n < 0) throw IllegalArgumentException("Tried to deduct $name a negative amount of money: $n")
        money -= n
    }

    fun addMoney(n: Int): Unit {
        if (n < 0) throw IllegalArgumentException("Tried to give $name a negative amount of money: $n")
        money += n
    }
}