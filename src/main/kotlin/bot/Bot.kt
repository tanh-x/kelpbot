package bot

import bot.command.BotCommand
import bot.command.CommandCategory
import dev.kord.common.Locale
import dev.kord.common.annotation.KordExperimental
import dev.kord.common.annotation.KordUnsafe
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.MessageChannelBehavior
import dev.kord.core.entity.Message
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import io.ktor.util.date.*
import utils.*
import java.io.File
import java.lang.Integer.min
import java.time.Instant
import kotlin.math.max
import kotlin.reflect.typeOf


object Bot {
    /**
     * Entry point of the bot
     */
    suspend fun main(): Unit {
        val kord = Kord(File("./token.txt").readLines().first())
        try {
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

                val duplicateInvocations: Map<String, Int> = CommandCategory.values()
                    .map { cat: CommandCategory -> cat.commands }
                    .reduce { acc: Array<BotCommand>, c: Array<BotCommand> -> acc + c }
                    .map { cmd: BotCommand -> cmd.invocations }
                    .reduce { acc: Array<String>, s: Array<String> -> acc + s }
                    .groupingBy { it }.eachCount()
                    .filter { pair: Map.Entry<String, Int> -> pair.value > 1 }
                if (duplicateInvocations.isNotEmpty()) {
                    throw IllegalStateException("Found duplicate command invocations: $duplicateInvocations")
                }
            }

        } catch (e: Exception) {
            @OptIn(KordUnsafe::class, KordExperimental::class)
            val reportingChannel: MessageChannelBehavior = kord.unsafe.messageChannel(
                Snowflake(
                    BotConstants.ERROR_REPORTING_CHANNEL
                )
            )
            var errorMsg: String =
                "Encountered exception \"${e::class.qualifiedName}\" at ${Instant.ofEpochMilli(getTimeMillis())}.\n" +
                "caused by: `${e.message}`\n" +
                "stack trace (truncated to 6 calls): \n```" + e.stackTrace.run {
                    return@run this.slice(0..(min(6, this.size))).joinToString("\n")
                } + "```\n"

            when (e) {
                else -> {
                    errorMsg += "no matching catch block declared, will terminate bot process"
                    reportingChannel.createMessage(errorMsg)
                    throw e
                }
            }
        }
    }

    /**
     * Given a message, fetch the correct command and execute it on the message. Handling of arguments
     * is delegated to [utils.getArgs]
     */
    private suspend fun Message.executeCommand(): Unit {
        getCommand(true)?.execute?.invoke(this, getArgs(content))
//            ?: reply("Unknown command: ${getArgs(this@executeCommand.content)[0]}")
            ?: ""
    }
}