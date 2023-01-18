package game.game_interfaces

import dev.kord.core.entity.Member
import dev.kord.core.entity.User
import game.AbstractPlayer
import utils.getUID
import java.lang.IllegalArgumentException

interface TurnBasedGame {
    var turn: Int
    var ply: Int
    val memberTurns: MutableMap<Int, Member>
    val turnMember: Member
        get() = memberTurns[ply]
            ?: throw IllegalStateException("Current ply (#$ply) doesn't correspond to any member")
    val turnPlayer: AbstractPlayer

    fun incrementPly(): Unit {
        ply += 1
        if (ply >= memberTurns.size) {
            incrementTurn()
            ply = 0
        }
    }

    fun Member.getTurnIndex(): Int? = memberTurns.entries.firstOrNull { pair: Map.Entry<Int, Member> ->
        pair.value == this
    }?.key

    fun AbstractPlayer.getTurnIndex(): Int = memberTurns.entries.firstOrNull { pair: Map.Entry<Int, Member> ->
        pair.value == this.member
    }?.key ?: throw IllegalArgumentException("Player is not part of this game")

    fun isCurrentTurn(user: User?): Boolean = user != null && user.getUID() == turnMember.getUID()
    fun isCurrentTurn(player: AbstractPlayer): Boolean = turnMember == player.member



    fun incrementTurn(): Unit {
        turn += 1
    }
}