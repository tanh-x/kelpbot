package game

import bot.command.BotCommand
import bot.command.CommandCategory
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

    companion object {
        private val COMMAND_LIST: Array<BotCommand> = CommandCategory.TURN_BASED_GAME.commands
    }
}