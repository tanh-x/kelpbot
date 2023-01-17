package bot.command

import dev.kord.core.entity.Message
import game.GameManager
import game.GameManager.fetchGameInChannel
import game.interfaces.DiceGame
import game.interfaces.TurnBasedGame
import utils.BotConstants
import utils.respond
import utils.reply

enum class CommandCategory(
    val commands: Array<BotCommand>,
    val categoryDescriptor: String = this.toString(),
    val isInvocableByMessage: (Message) -> Boolean = { true }
) {
    DUMMY(commands = arrayOf(
        BotCommand(
            invocations = arrayOf("help", "h", "?"),
            descriptor = "",
            /*
            * Iterates over every category defined here, then iterate over every command in it,
            * then print out the possible invocation prompts as well as their descriptors, separated
            * by categories.
            * */
            execute = { msg: Message, _: Array<String> ->
                var output: String = ""
                CommandCategory.values().forEach { cat: CommandCategory ->
                    output += cat.categoryDescriptor + "\n"
                    cat.commands.forEach { cmd: BotCommand ->
                        output += " - ${cmd.invocations.contentToString()}: ${cmd.descriptor}\n"
                    }
                    output += "\n"
                }
                msg.reply(output)
            }
        ),

        BotCommand(
            invocations = arrayOf("goongus", "glungus", "grungus", "grunglus"),
            descriptor = "",
            execute = { msg: Message, _: Array<String> ->
                msg.reply("yoeure a goongus")
            }
        )),
        categoryDescriptor = "Dummy commands"
    ),

    BOT_ADMIN_COMMANDS(commands = arrayOf(
        BotCommand(
            invocations = arrayOf("displayname"),
            descriptor = this.toString(),
            execute = { msg: Message, _: Array<String> ->
                msg.reply(msg.getAuthorAsMember()!!.displayName)
            }
        ),
        BotCommand(
            invocations = arrayOf("kys"),
            descriptor = "immediately throws an error, crashing the bot",
            execute = { msg: Message, _: Array<String> ->
                msg.respond(":skull:")
                throw UnknownError("Brutally murdered by kebbebr")
            }
        )),
        categoryDescriptor = "Debug commands",
        isInvocableByMessage = { msg: Message -> msg.author?.id?.value in BotConstants.BOT_ADMINS_LIST }
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
                msg.reply((GameManager.getGame(msg.channelId.value) ?: "No ongoing games").toString())
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
        categoryDescriptor = "General game commands",
        isInvocableByMessage = { msg: Message ->
            /*
            * Allows us to not carry out null checks within the commands' execute functions
            * regarding whether a game exists within the channel that the command was invoked in,
            * since the execute block never gets called if we are already guarded by this check.
            * */
            GameManager.hasGame(msg.channelId.value)
        }
    ),

    TURN_BASED_GAME(commands = arrayOf(
        BotCommand(
            invocations = arrayOf("end", "et"),
            descriptor = "Ends the current player's turn, increments ply by 1",
            execute = { msg: Message, _: Array<String> ->
                val G: TurnBasedGame = msg.fetchGameInChannel() as TurnBasedGame
                G.incrementPly()
            },
            isInvocable = { msg: Message ->
                val G: TurnBasedGame = msg.fetchGameInChannel() as TurnBasedGame
                G.currentPlayer.id.value == msg.author?.id?.value
            }
        ),
        BotCommand(
            invocations = arrayOf("current", "ct"),
            descriptor = "Gets the current turn's player",
            execute = { msg: Message, _: Array<String> ->
                val G: TurnBasedGame = msg.fetchGameInChannel() as TurnBasedGame
                msg.reply(G.currentPlayer.displayName)
            }
        )),
        categoryDescriptor = "Commands for turn-based games",
        isInvocableByMessage = { msg: Message ->
            /*
            * Allows us to not carry out null checks within the commands' execute functions
            * regarding whether a game exists within the channel that the command was invoked in,
            * since the execute block never gets called if we are already guarded by this check.
            * */
            msg.fetchGameInChannel() is TurnBasedGame
        }
    ),

    DICE_GAME(commands = arrayOf(
        BotCommand(
            invocations = arrayOf("roll", "r"),
            descriptor = "Rolls the dice, if possible",
            execute = { msg: Message, _: Array<String> ->
                val G: DiceGame = msg.fetchGameInChannel() as DiceGame
                msg.reply(G.roll().toString())
            }
        )),
        categoryDescriptor = "Commands for games with dices",
        isInvocableByMessage = { msg: Message ->
            /*
            * Allows us to not carry out null checks within the commands' execute functions
            * regarding whether a game exists within the channel that the command was invoked in,
            * since the execute block never gets called if we are already guarded by this check.
            * */
            msg.fetchGameInChannel() is DiceGame
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
//object CommandList {l1
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