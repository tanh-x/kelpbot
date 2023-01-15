package game

import bot.command.BotCommand
import bot.command.CommandList
import dev.kord.core.entity.Member
import dev.kord.core.entity.Message
import dev.kord.core.entity.channel.MessageChannel

abstract class AbstractGame(
    initialPlayerList: MutableSet<Member>,
    hostChannel: MessageChannel,
) {
    var playerList: MutableSet<Member> = initialPlayerList
        private set

    var isOngoing: Boolean = false
        private set

    var isJoinable: Boolean = true
        protected set

    private val channel: MessageChannel = hostChannel

    open suspend fun startGame() {
        isOngoing = true
        sendMessage("Starting game with ${playerList.size} players")
    }

    open suspend fun addPlayer(player: Member): Boolean {
        if (!isJoinable) {
            sendMessage("Could not add player ${player.nickname} into this game")
            return false
        }

        if (!playerList.add(player)) {
            sendMessage("${player.nickname} is already in this game")
            return false
        }

        sendMessage("Added ${player.nickname} to this game")
        return true
    }

    open fun getFormattedPlayerList(): String {
        return playerList.map { p: Member -> p.nickname } .joinToString(", ")
    }

    protected suspend fun sendMessage(content: String) {
        channel.createMessage(content)
    }

    abstract fun getCommandList(): Array<BotCommand>
}