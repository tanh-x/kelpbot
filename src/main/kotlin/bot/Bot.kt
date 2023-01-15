package bot

import bot.command.BotCommand
import bot.command.CommandList
import dev.kord.core.Kord
import dev.kord.core.behavior.reply
import dev.kord.core.entity.Member
import dev.kord.core.entity.Message
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import game.AbstractGame
import util.BotConstants

class Bot(authToken: String) {
    private val token: String = authToken
    private val gamesList: MutableMap<ULong, AbstractGame> = mutableMapOf()

    /**
     * Entry point the bot
     */
    suspend fun main() {
        val kord = Kord(token)

        kord.on<MessageCreateEvent> {
            if (message.author == null || message.author!!.isBot ||
                !message.content.startsWith(BotConstants.COMMAND_PREFIX) ||
                message.content.length > BotConstants.MESSAGE_LENGTH_LIMIT
            ) return@on

            parseCommand(message)
        }

        kord.login {
            @OptIn(PrivilegedIntent::class)
            this.intents += Intent.MessageContent
        }
    }

    /**
     * Given a message, fetch the correct command and execute it on the message. Handling of arguments
     * is delegated to the dispatched function corresponding to the command.
     *
     * @param msg The message object to parse
     */
    private suspend fun parseCommand(msg: Message) {
        val userInvocation: String = msg.content.substringBefore(" ").substring(1)

        val validCommandsInContext: Array<BotCommand> =
            (gamesList[msg.channelId.value]?.getCommandList() ?: emptyArray()) +
            CommandList.DUMMY_COMMANDS

        val invokedCmd: BotCommand? = validCommandsInContext.firstOrNull { cmd: BotCommand ->
            return@firstOrNull userInvocation in cmd.invocation
        }

        if (invokedCmd != null) invokedCmd.execute(msg)
        else msg.reply { content = "Unknown command: $userInvocation" }
    }

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
