package bot.command

import dev.kord.core.behavior.reply
import dev.kord.core.entity.Message
import game.AbstractGame
import game.GameManager
import game.GameManager.fetchGameInChannel
import game.game_interfaces.DiceGame
import game.game_interfaces.TurnBasedGame
import game.monopoly_game.MonopolyGame
import utils.BotConstants
import utils.formatDiceRoll
import utils.reply
import utils.respond
import kotlin.io.path.Path

enum class CommandCategory(
    val commands: Array<BotCommand>,
    val categoryDescriptor: String = this.toString(),
    val isInvocableByMessage: (Message) -> Boolean = { _ -> true }
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
            invocations = arrayOf("echo", "$"),
            descriptor = "replies with the list of arguments (Array<String>) passed into the command",
            execute = { msg: Message, args: Array<String> ->
                msg.reply(args.contentToString())
            }
        ),
        BotCommand(
            invocations = arrayOf("displayname", "self"),
            descriptor = this.toString(),
            execute = { msg: Message, _: Array<String> ->
                msg.reply(msg.getAuthorAsMember()!!.displayName)
            }
        ),
        BotCommand(
            invocations = arrayOf("kys", "^#"),
            descriptor = "immediately throws an error, crashing the bot",
            execute = { _: Message, _: Array<String> ->
                throw UnknownError("Brutally murdered by kebbebr")
            }
        ),
        BotCommand(
            invocations = arrayOf("path", "/"),
            descriptor = "converts a given relative path to absolute",
            execute = { msg: Message, args: Array<String> ->
                if (args.size < 2) return@BotCommand
                msg.reply(Path(args[1]).toAbsolutePath().toString())
            }
        ),
        BotCommand(
            invocations = arrayOf("formatdiceroll"),
            descriptor = "tests the helper method utils.formatDiceRoll ((Array<Int>, Boolean?) -> String)",
            execute = { msg: Message, args: Array<String> ->
                msg.reply(formatDiceRoll(ArrayList(args.mapNotNull { arg: String -> arg.toIntOrNull() })))
            }
        )),
        categoryDescriptor = "Debug commands",
        isInvocableByMessage = { msg: Message -> msg.author?.id?.value in BotConstants.BOT_ADMINS_LIST }
    ),

    GAME_MANAGEMENT(commands = arrayOf(
        BotCommand(
            invocations = arrayOf("here", "ongoing", "game", "~"),
            descriptor = "Find and display the game in this channel, if it exists",
            execute = { msg: Message, _: Array<String> ->
                msg.reply( (msg.fetchGameInChannel() ?: "No ongoing games in this channel").toString() )
            }
        ),
        BotCommand(
            invocations = arrayOf("details", "*"),
            descriptor = "Get the detailed state of the currently ongoing game",
            execute = { msg: Message, _: Array<String> ->
                val G: AbstractGame? = msg.fetchGameInChannel()
                if (G == null) {
                    msg.reply("No ongoing games in this channel")
                    return@BotCommand
                }
                msg.respond( G.toString() + "\n" +  G.getDetailedGameString() )
            },
        ),
        BotCommand(
            invocations = arrayOf("join", "j", "+"),
            descriptor = "Join the currently ongoing game in this channel, if possible",
            execute = { msg: Message, _: Array<String> ->
                val G: AbstractGame? = msg.fetchGameInChannel()
                if (G == null) {
                    msg.reply("No ongoing games in this channel")
                    return@BotCommand
                }
                G.addMember(msg.getAuthorAsMember()!!, isHost = false)
            },
            isInvocable = { msg: Message ->
                true
            }
        ),
        BotCommand(
            invocations = arrayOf("monopoly", "+m"),
            descriptor = "Creates a new monopoly game, if there are no games running here",
            execute = { msg: Message, _: Array<String> ->
                val newGame: MonopolyGame = MonopolyGame(mutableMapOf(), msg.getChannel())
                newGame.addMember(msg.getAuthorAsMember()!!, isHost = true)
                GameManager.addNewGame(msg.channelId.value, newGame).also { success: Boolean ->
                    if (success) return@also
                    // (Should never happen) Else, there is somehow already a game
                    msg.reply("There is already a game running in this channel")
                }
            },
            isInvocable = { msg: Message ->
                msg.fetchGameInChannel() == null
            }
        )),
        categoryDescriptor = "Game management commands"
    ),

    ABSTRACT_GAME(commands = arrayOf(
        BotCommand(
            invocations = arrayOf("start", "!#"),
            descriptor = "Starts the game in this channel.",
            execute = { msg: Message, _: Array<String> ->
                msg.fetchGameInChannel()?.startGame()
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
            invocations = arrayOf("end", "et", "#"),
            descriptor = "Ends the current player's turn, increments ply by 1",
            execute = { msg: Message, _: Array<String> ->
                val G: TurnBasedGame = msg.fetchGameInChannel() as TurnBasedGame
                G.incrementPly()
            },
            isInvocable = { msg: Message ->
                (msg.fetchGameInChannel() as TurnBasedGame).isCurrentTurn(msg.author)
            }
        ),
        BotCommand(
            invocations = arrayOf("current", "ct", "@"),
            descriptor = "Gets the current turn's player",
            execute = { msg: Message, _: Array<String> ->
                val G: TurnBasedGame = msg.fetchGameInChannel() as TurnBasedGame
                msg.reply(G.turnMember.displayName)
            }
        )),
        categoryDescriptor = "Commands for turn-based games",
        isInvocableByMessage = { msg: Message ->
            val G: AbstractGame? = msg.fetchGameInChannel()
            G is TurnBasedGame && G.isOngoing
        }
    ),

    DICE_GAME(commands = arrayOf(
        BotCommand(
            invocations = arrayOf("roll", "r", "="),
            descriptor = "Rolls the dice, if possible",
            execute = { msg: Message, _: Array<String> ->
                val G: DiceGame = msg.fetchGameInChannel() as DiceGame
                msg.reply(formatDiceRoll(G.roll(2, 6)))
            }
        )),
        categoryDescriptor = "Commands for games with dices",
        isInvocableByMessage = { msg: Message ->
            val G: AbstractGame? = msg.fetchGameInChannel()
            G is DiceGame && G.isOngoing && (G !is TurnBasedGame || G.isCurrentTurn(msg.author))
        }
    ),

    MONOPOLY_GAME(commands = arrayOf(
        BotCommand(
            invocations = arrayOf("buy", "purchase", "%+"),
            descriptor = "Purchase the square that the player is currently standing on",
            execute = { msg: Message, _: Array<String> ->
                val G: MonopolyGame = msg.fetchGameInChannel() as MonopolyGame
            },
            isInvocable = { msg: Message ->
                (msg.fetchGameInChannel() as TurnBasedGame).isCurrentTurn(msg.author)
            }
        ),
        BotCommand(
            invocations = arrayOf("quickroll", "=#"),
            descriptor = "Roll and end turn",
            execute = { msg: Message, _: Array<String> ->
                val G: MonopolyGame = msg.fetchGameInChannel() as MonopolyGame
                msg.reply(formatDiceRoll(G.roll(2, 6)))
                G.incrementPly()
            },
            isInvocable = { msg: Message ->
                (msg.fetchGameInChannel() as TurnBasedGame).isCurrentTurn(msg.author)
            }
        )),
        categoryDescriptor = "Commands for Monopoly game",
        isInvocableByMessage = { msg: Message ->
            val G: AbstractGame? = msg.fetchGameInChannel()
            G is MonopolyGame && G.isOngoing
        }
    ),

    TEST_GAME(commands = arrayOf(
        BotCommand(
            invocations = arrayOf("do"),
            descriptor = "",
            execute = { _, _ -> }
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
//        BotCommand(invocation = arrayOf("create", "new", "+"), "") { msg: Message, args: Array<String> ->
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
//        BotCommand(invocation = arrayOf(), "") { msg: Message, args: Array<String> ->
//
//        }
//    )
//}