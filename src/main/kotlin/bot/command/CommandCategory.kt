package bot.command

import dev.kord.core.behavior.reply
import dev.kord.core.entity.Message
import game.GameManager
import game.GameManager.getGame
import game.TurnBasedGame
import utils.respond
import utils.reply

enum class CommandCategory(
    val commands: Array<BotCommand>,
    val categoryDescriptor: String = this.toString(),
    val isInvocableByMessage: (src: Message) -> Boolean = { true }
) {
    DUMMY(commands = arrayOf(
        BotCommand(
            invocations = arrayOf("help", "h", "?"),
            descriptor = "",
            execute = { msg: Message, _: Array<String> ->
                msg.respond("no help")
            }
        ),

        BotCommand(
            invocations = arrayOf("goongus", "glungus", "grungus", "grunglus"),
            descriptor = "",
            execute = { msg: Message, _: Array<String> ->
                msg.reply("yoeure a goongus")
            }
        )),
        categoryDescriptor = "dummy commands"
    ),

    GAME_MANAGEMENT(commands = arrayOf(
        BotCommand(
            invocations = arrayOf("create", "new", "+"),
            descriptor = "",
            execute = { msg: Message, vargs: Array<String> ->

            }
        ),
        BotCommand(
            invocations = arrayOf("here", "ongoing", "game"),
            descriptor = "Find and display the game in this channel, if it exists",
            execute = { msg: Message, _: Array<String> ->
                msg.reply {
                    content = msg.getChannel().getGame()?.toString()
                        ?: "There are currently no games being hosted in this channel"
                }
            }
        )),
        categoryDescriptor = "Game management commands"
    ),

    ABSTRACT_GAME(commands = arrayOf(
        BotCommand(
            invocations = arrayOf("start"),
            descriptor = "Starts the game in this channel.",
            execute = { msg: Message, _: Array<String> ->

            }
        )),
        categoryDescriptor = "General game commands"
    ),

    TURN_BASED_GAME(commands = arrayOf(
        BotCommand(
            invocations = arrayOf("end", "et"),
            descriptor = "Ends the current player's turn, increments ply by 1",
            execute = { msg: Message, _: Array<String> ->
                val G: TurnBasedGame = msg.getChannel().getGame() as TurnBasedGame

            }
        ),
        BotCommand(
            invocations = arrayOf("current", "ct"),
            descriptor = "Gets the current turn's player",
            execute = { msg: Message, _: Array<String> ->
                val G: TurnBasedGame = msg.getChannel().getGame() as TurnBasedGame
            }
        )),
        categoryDescriptor = "Commands for turn-based games",
        isInvocableByMessage = { msg: Message ->
            GameManager.getGame(msg.channelId.value) is TurnBasedGame
        }
    ),

    TEST_GAME(commands = arrayOf(
        BotCommand(
            invocations = arrayOf("do"),
            descriptor = "",
            execute = { msg: Message, vargs: Array<String> ->

            }
        )
    ));
}


//
//object CommandList {
//    @JvmStatic
//    val DUMMY_COMMANDS: Array<BotCommand> = arrayOf(
//        BotCommand(invocation = arrayOf("help", "h", "?"), "") { msg: Message, _: Array<String> ->
//            msg.respond("no help")
//        },
//        BotCommand(
//            invocation = arrayOf("goongus", "glungus", "grungus", "grunglus"),
//            ""
//        ) { msg: Message, _: Array<String> ->
//            msg.respond("goongus")
//        },
//        BotCommand(invocation = arrayOf("nothing"))
//    )
//
//    @JvmStatic
//    val GLOBAL_GAME_COMMANDS: Array<BotCommand> = arrayOf(
//        BotCommand(invocation = arrayOf("create", "new", "+"), "") { msg: Message, vargs: Array<String> ->
//
//        }
//    )
//
//    @JvmStatic
//    val ABSTRACT_GAME_COMMANDS: Array<BotCommand> = arrayOf(
//        BotCommand(invocation = arrayOf("start"), "Starts the game in this channel.")
//    )
//
//    @JvmStatic
//    val TURN_BASED_GAME_COMMANDS: Array<BotCommand> = arrayOf(
//        BotCommand(
//            invocation = arrayOf("end", "et"),
//            "Ends the current player's turn, increments ply by 1"
//        ) { msg: Message, _: Array<String> ->
//
//        }
//    )
//
//    @JvmStatic
//    val TEST_GAME_COMMANDS: Array<BotCommand> = arrayOf(
//        BotCommand(invocation = arrayOf(), "") { msg: Message, vargs: Array<String> ->
//
//        }
//    )
//}