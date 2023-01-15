package game.TestGame

import dev.kord.core.entity.User
import dev.kord.core.entity.channel.MessageChannel
import game.AbstractGame

class TestGame(
    playerList: MutableSet<User>,
    hostChannel: MessageChannel
): AbstractGame(playerList, hostChannel) {
    override suspend fun startGame() {
        super.startGame()
    }
}