package game

import dev.kord.core.entity.User
import dev.kord.core.entity.channel.MessageChannel

abstract class AbstractGame(
    initialPlayerList: MutableSet<User>,
    hostChannel: MessageChannel,
) {
    var playerList: MutableSet<User> = initialPlayerList
        private set

    var isOngoing: Boolean = false
        private set

    var isJoinable: Boolean = true
        protected set

    val channel: MessageChannel = hostChannel

    open suspend fun startGame() {
        isOngoing = true
        channel.createMessage("Starting game with ${playerList.size} players")
    }

    open suspend fun addPlayer(player: User): Boolean {
        if (!isJoinable) {
            channel.createMessage("Could not add player ${player.tag} into this game")
            return false
        }

        if (!playerList.add(player)) {
            channel.createMessage("${player.tag} is already in this game")
            return false
        }

        channel.createMessage("Added ${player.tag} to this game")
        return true
    }
}