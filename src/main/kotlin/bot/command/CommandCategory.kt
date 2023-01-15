package bot.command

import dev.kord.core.entity.Message
import util.respond

enum class CommandCategory(
    val commands: Array<BotCommand>,
    val categoryDescriptor: String = this.toString(),
    val isInvocableByMessage: (src: Message) -> Boolean = { true }
) {
    DUMMY(
        commands = arrayOf(
            BotCommand(
                invocations = arrayOf("help", "h", "?"),
                ""
            ) { msg: Message, _: Array<String> ->
                msg.respond("no help")
            },

            BotCommand(
                invocations = arrayOf("goongus", "glungus", "grungus", "grunglus"),
                ""
            ) { msg: Message, _: Array<String> ->
                msg.respond("goongus")
            },
            BotCommand(invocations = arrayOf("nothing"))
        ),
    ),

    GAME_MANAGEMENT(
        commands = arrayOf(
            BotCommand(
                invocations = arrayOf("create", "new", "+"),
                ""
            ) { msg: Message, vargs: Array<String> ->

            }
        )
    ),

    ABSTRACT_GAME(
        commands = arrayOf(
            BotCommand(
                invocations = arrayOf("start"),
                "Starts the game in this channel."
            ) { msg: Message, _: Array<String> ->

            }
        )
    ),

    TURN_BASED_GAME(
        commands = arrayOf(
            BotCommand(
                invocations = arrayOf("end", "et"),
                "Ends the current player's turn, increments ply by 1"
            ) { msg: Message, _: Array<String> ->

            }
        )
    ),

    TEST_GAME(
        commands = arrayOf(
            BotCommand(
                invocations = arrayOf("do"),
                ""
            ) { msg: Message, vargs: Array<String> ->

            }
        )
    );
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