package bot

import bot.command.BotCommand
import bot.command.CommandList
import dev.kord.core.Kord
import dev.kord.core.behavior.reply
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

    suspend fun main() {
        val kord = Kord(token)

        kord.on<MessageCreateEvent> {
            if (message.author == null || message.author!!.isBot ||
                !message.content.startsWith(BotConstants.COMMAND_PREFIX) ||
                message.content.length < BotConstants.MESSAGE_LENGTH_LIMIT
            ) return@on

            parseCommand(message)
        }

        kord.login {
            @OptIn(PrivilegedIntent::class)
            this.intents += Intent.MessageContent
        }
    }

    private suspend fun parseCommand(msg: Message) {
        val userInvocation: String = msg.content.substringBefore(" ").substring(1)

        val validCommandsInContext: Array<BotCommand> =
            CommandList.DUMMY_COMMANDS +
            (gamesList[msg.channelId.value]?.getCommandList() ?: arrayOf())

        val invokedCmd: BotCommand? = validCommandsInContext.firstOrNull { cmd: BotCommand ->
            return@firstOrNull userInvocation in cmd.invocation
        }

        if (invokedCmd != null) invokedCmd.execute(msg)
        else msg.reply { content = "Unknown command: $userInvocation" }
    }
}
