package bot.command

import dev.kord.core.entity.Message

interface Command {
    suspend fun execute(msg: Message)
}