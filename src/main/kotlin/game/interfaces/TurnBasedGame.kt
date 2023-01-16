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

    fun Member.getTurnIndex(): Int? = playerTurns.entries.firstOrNull { pair -> pair.value == this }?.key

    fun Member.isCurrentTurn(): Boolean = this.getTurnIndex() == ply

    fun incrementTurn(): Unit {
        turn += 1
    }
}