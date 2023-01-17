package bot

import bot.command.CommandCategory
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.MessageChannelBehavior
import dev.kord.core.entity.Message
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import io.ktor.util.date.*
import tests.InitValidationTests
import utils.*
import utils.BotConstants.COMMAND_PREFIX
import utils.BotConstants.ERROR_REPORTING_CHANNEL
import utils.BotConstants.MESSAGE_LENGTH_LIMIT
import java.io.File
import java.lang.Error
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import java.lang.Integer.min
import java.time.Instant


object Bot {
    init {
        try {
            InitValidationTests.testCommandDuplication(CommandCategory.values())
        } catch (err: Throwable) {
            when (err) {
                is Exception -> println("Exception occurred during initial tests: ")
                is Error -> println("Error occurred during initial tests: ")
                else -> println("Unknown error occurred during initial tests: ")
            }
            throw err
        }
    }

    /**
     * Entry point of the bot
     */
    suspend fun main(): Unit {
        val kord = Kord(File("./token.txt").readLines().first())

        kord.on<MessageCreateEvent> {
            try {
                if (message.author == null || message.author!!.isBot ||
                    !message.content.startsWith(COMMAND_PREFIX) ||
                    message.content.length > MESSAGE_LENGTH_LIMIT
                ) return@on
                message.executeCommand()
            } catch (err: Throwable) {
                println("[!!] Error occurred during runtime at ${Instant.ofEpochMilli(getTimeMillis())}")
                err.printStackTrace()
                if (handleRuntimeThrowable(err, kord.getChannel(ERROR_REPORTING_CHANNEL))) {
                    kord.shutdown()
                }
            }
        }

        kord.login {
            try {
                @OptIn(PrivilegedIntent::class)
                this.intents += Intent.MessageContent
            } catch (err: Throwable) {
                println("[!!] Error occurred during login")
                err.printStackTrace()
                if (handleLoginThrowable(err)) {
                    kord.shutdown()
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
    }

    /**
     * @return Whether we should terminate the runtime
     */
    private suspend fun handleRuntimeThrowable(
        err: Throwable,
        reportingChannel: MessageChannelBehavior? = null
    ): Boolean {
        var errorMsg: String =
            "Encountered error \"${err::class.qualifiedName}\"" +
            "at ${Instant.ofEpochMilli(getTimeMillis())}.\n" +
            "caused by: `${err.message}`\n" +
            "stack trace (truncated to 6 calls): \n```" + err.stackTrace.run {
                return@run this.slice(0..(min(6, this.size))).joinToString("\n")
            } + "```"

        errorMsg += when (err) {
            is IllegalStateException -> "bot has illegal state, exiting to avoid unpredictable behavior"
            is Exception -> "no matching catch block declared, will terminate bot process"
            else -> "unrecoverable error, exiting"
        }
        reportingChannel?.createMessage(errorMsg)

        return when (err) {
            is IllegalArgumentException -> false
            else -> true
        }
    }

    /**
     * @return Whether we should terminate the runtime
     */
    private fun handleLoginThrowable(err: Throwable): Boolean {
        println("Exception/Error occurred during login")
        return true
    }
}