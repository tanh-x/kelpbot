package bot.command

import dev.kord.core.entity.Message
import util.respond

enum class DummyCommands: Command {
    HELP {
        override suspend fun execute(msg: Message) {
            msg.respond("no help")
        }
    },
}