package game.TestGame

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
}