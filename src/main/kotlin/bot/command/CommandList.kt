package bot.command

import dev.kord.core.entity.Message
import util.respond

object CommandList {
    @JvmStatic
    val DUMMY_COMMANDS: Array<BotCommand> = arrayOf(
        BotCommand(invocation = arrayOf("help", "h", "?")) { msg: Message ->
            msg.respond("no help")
        },
        BotCommand(invocation = arrayOf("goongus", "glungus", "grungus", "grunglus")) { msg: Message ->
            msg.respond("goongus")
        }
    )

    @JvmStatic
    val TEST_GAME_COMMANDS: Array<BotCommand> = arrayOf(
        BotCommand(invocation = arrayOf()) { msg: Message ->

        }
    )
}
