package game

import bot.command.BotCommand
import bot.command.CommandCategory
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.entity.Member
import dev.kord.core.entity.channel.MessageChannel
import dev.kord.rest.builder.message.create.UserMessageCreateBuilder
import io.ktor.util.date.*

abstract class AbstractGame(
    initialPlayerList: MutableSet<Member>,
    hostChannel: MessageChannel,
) {
    /**
     * Stores the list of players participating in the game. Uses a [MutableSet] of [Member]s
     * as we want to access server-specific information.
     */
    var playerList: MutableSet<Member> = initialPlayerList
        private set

    /**
     * Whether the game is still in progress
     */
    var isOngoing: Boolean = false
        private set

    /**
     * Whether new players can still join
     */
    var isJoinable: Boolean = true
        protected set

    /**
     * Timestamp of the game's creation (recorded when the class is initialised, not when
     * the game is started). Used to disband abandoned games unless forcibly kept alive
     */
    val createdOnMilis: Long = getTimeMillis()

    private val channel: MessageChannel = hostChannel

    /**
     * Attempts to start the game, called by the user with the start command
     */
    open suspend fun startGame() {
        isOngoing = true
        sendMessage("Starting game with ${playerList.size} players")
    }

    /**
     * Attempts to add the specified user to the game
     */
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

    /**
     * More succinct method call to send a message to the channel that the game is hosted on
     */
    protected suspend fun sendMessage(content: String) {
        channel.createMessage(content)
    }

    protected suspend fun sendMessage(builder: UserMessageCreateBuilder.() -> Unit) {
        channel.createMessage(builder)
    }

    open fun getCommandList(): Array<BotCommand> = GAME_COMMAND_LIST

    companion object {
        private val GAME_COMMAND_LIST: Array<BotCommand> = CommandCategory.ABSTRACT_GAME.commands
    }
}