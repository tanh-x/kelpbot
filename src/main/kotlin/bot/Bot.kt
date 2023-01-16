package bot

import dev.kord.core.Kord
import dev.kord.core.entity.Message
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import utils.*
import java.io.File


object Bot {
    /**
     * Entry point of the bot
     */
    suspend fun main(): Unit {
        val kord = Kord(File("./token.txt").readLines().first())

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
     * is delegated to [utils.getArgs]
     */
    private suspend fun Message.executeCommand(): Unit {
        getCommand(true)?.execute?.invoke(this, getArgs(content))
            ?: reply("Unknown command: ${getArgs(this@executeCommand.content)[0]}")
    }
}