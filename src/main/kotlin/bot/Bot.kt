package bot

import dev.kord.core.Kord
import dev.kord.core.behavior.reply
import dev.kord.core.entity.Message
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import game.AbstractGame
import util.BotConstants
import util.getCommand
import util.getArgs
import java.util.*

class Bot(authToken: String) {
    private val token: String = authToken
    private val gamesList: MutableMap<ULong, AbstractGame> = mutableMapOf()

    /**
     * Entry point of the bot
     */
    suspend fun main() {
        val kord = Kord(token)

        kord.on<MessageCreateEvent> {
            if (message.author == null || message.author!!.isBot ||
                !message.content.startsWith(BotConstants.COMMAND_PREFIX) ||
                message.content.length > BotConstants.MESSAGE_LENGTH_LIMIT
            ) return@on

            message.executeCommand()
        }

        kord.login {
            @OptIn(PrivilegedIntent::class)
            this.intents += Intent.MessageContent
        }
    }

    /**
     * Given a message, fetch the correct command and execute it on the message. Handling of arguments
     * is delegated to [util.getArgs]
     */
    private suspend fun Message.executeCommand() = getCommand(true)
        ?.execute?.invoke(this, getArgs(content))
        ?: reply { content = "Unknown command: ${getArgs(this@executeCommand.content)[0]}" }

    /**
     * @param channelId the ID of the channel to associate this game with
     * @param game The game instance
     * @return True if the [channelId] key is not already associated with a game, false if there is
     * already a game in this channel
     */
    fun createNewGame(channelId: ULong, game: AbstractGame): Boolean {
        gamesList.putIfAbsent(channelId, game).also { return it == null }
    }
}
