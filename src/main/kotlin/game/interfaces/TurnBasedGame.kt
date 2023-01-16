package game.interfaces

import dev.kord.core.entity.Member

interface TurnBasedGame {
    var turn: Int
    var ply: Int
    val playerTurns: MutableMap<Int, Member>

    fun incrementPly(): Unit {
        ply += 1
        if (ply >= playerTurns.size) {
            incrementTurn()
            ply = 0
        }
    }

    fun incrementTurn(): Unit {
        turn += 1
    }
}