package game.interfaces

import dev.kord.core.entity.Member

interface TurnBasedGame {
    var turn: Int
    var ply: Int
    val playerTurns: MutableMap<Int, Member>
    val currentPlayer: Member
        get() = playerTurns[ply] ?: throw IllegalStateException("Could not find player for ply #$ply")

    fun incrementPly(): Unit {
        ply += 1
        if (ply >= playerTurns.size) {
            incrementTurn()
            ply = 0
        }
    }

    fun getTurnIndex(m: Member): Int? = playerTurns.entries.firstOrNull { pair -> pair.value == m }?.key

    fun isCurrentTurn(m: Member): Boolean = getTurnIndex(m) == ply

    fun incrementTurn(): Unit {
        turn += 1
    }
}