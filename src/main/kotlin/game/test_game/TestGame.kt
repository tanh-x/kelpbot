package game.test_game

import bot.command.BotCommand
import bot.command.CommandCategory
import dev.kord.core.entity.Member
import dev.kord.core.entity.channel.MessageChannel
import game.AbstractGame
import game.TurnBasedGame
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class TestGame(
    playerList: MutableSet<Member>,
    hostChannel: MessageChannel,
): AbstractGame(playerList, hostChannel), TurnBasedGame {
    override var turn: Int = 0
    override var ply: Int = 0

    override val playerTurns: MutableMap<Int, Member> = mutableMapOf()


    override fun incrementPly(): Unit = runBlocking {
        super.incrementPly()
        launch { sendMessage("End turn") }
    }

    override fun startGame(): Unit = runBlocking {
        launch { sendMessage("Starting the test game") }
    }

    override fun getCommandList(): Array<BotCommand> = super.getCommandList() + COMMAND_LIST

    companion object {
        private val COMMAND_LIST: Array<BotCommand> = CommandCategory.TEST_GAME.commands
    }
}