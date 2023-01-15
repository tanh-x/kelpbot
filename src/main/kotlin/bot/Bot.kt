package bot

import dev.kord.core.Kord
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import util.BotConstants

class Bot(authToken: String) {
    private val token: String = authToken

    suspend fun main() {
        val kord = Kord(token)

        kord.on<MessageCreateEvent> {
            if (message.author == null ||
                message.author!!.isBot ||
                !message.content.startsWith(BotConstants.COMMAND_PREFIX)
            ) return@on

        }

        kord.login {
            @OptIn(PrivilegedIntent::class)
            this.intents += Intent.MessageContent
            println("Logged in \n")
        }
    }
}
