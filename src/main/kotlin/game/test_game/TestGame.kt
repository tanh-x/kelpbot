package game.test_game

import bot.command.BotCommand
import bot.command.CommandCategory
import dev.kord.core.entity.Member
import dev.kord.core.entity.channel.MessageChannel
import game.AbstractGame
import game.TurnBasedGame

class TestGame(
    playerList: MutableSet<Member>,
    hostChannel: MessageChannel,
): AbstractGame(playerList, hostChannel), TurnBasedGame {
    override val turn: Int = 0
    override val ply: Int = 0

    override suspend fun startGame() {
        super.startGame()
        sendMessage("Created a test game")
    }

    override fun getCommandList(): Array<BotCommand> = super.getCommandList() + GAME_COMMAND_LIST

    companion object {
        private val GAME_COMMAND_LIST: Array<BotCommand> = CommandCategory.TEST_GAME.commands
    }
}