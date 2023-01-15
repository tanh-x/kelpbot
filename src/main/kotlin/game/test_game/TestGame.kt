package game.test_game

import bot.command.BotCommand
import bot.command.CommandList
import dev.kord.core.entity.Member
import dev.kord.core.entity.channel.MessageChannel
import game.AbstractGame

class TestGame(
    playerList: MutableSet<Member>,
    hostChannel: MessageChannel
): AbstractGame(playerList, hostChannel) {
    override suspend fun startGame() {
        super.startGame()
        sendMessage("Created a test game")
    }

    override fun getCommandList(): Array<BotCommand> = GAME_COMMAND_LIST

    companion object {
        private val GAME_COMMAND_LIST: Array<BotCommand> = CommandList.TEST_GAME_COMMANDS
    }
}