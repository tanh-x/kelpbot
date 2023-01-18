package bot.command

import dev.kord.core.entity.Message
import game.AbstractGame
import game.AbstractPlayer
import game.GameManager
import game.GameManager.fetchGameInChannel
import game.game_interfaces.DiceGame
import game.game_interfaces.TurnBasedGame
import game.monopoly_game.MonopolyGame
import utils.BotConstants
import utils.formatDiceRoll
import utils.getUID
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
        ),
        BotCommand(
            invocations = arrayOf("formatdiceroll"),
            descriptor = "tests the helper method utils.formatDiceRoll ((Array<Int>, Boolean?) -> String)",
            execute = { msg: Message, vargs: Array<String> ->
                msg.reply(formatDiceRoll(ArrayList(vargs.mapNotNull { arg: String -> arg.toIntOrNull() })))
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
            invocations = arrayOf("end", "et"),
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
            invocations = arrayOf("current", "ct"),
            descriptor = "Gets the current turn's player",
            execute = { msg: Message, _: Array<String> ->
                val G: TurnBasedGame = msg.fetchGameInChannel() as TurnBasedGame
                msg.reply(G.turnMember.displayName)
            }
        )),
        categoryDescriptor = "Commands for turn-based games",
        isInvocableByMessage = { msg: Message ->
            msg.fetchGameInChannel() is TurnBasedGame
        }
    ),

    DICE_GAME(commands = arrayOf(
        BotCommand(
            invocations = arrayOf("roll", "r"),
            descriptor = "Rolls the dice, if possible",
            execute = { msg: Message, _: Array<String> ->
                val G: DiceGame = msg.fetchGameInChannel() as DiceGame
                msg.reply(formatDiceRoll(G.roll()))
            }
        )),
        categoryDescriptor = "Commands for games with dices",
        isInvocableByMessage = { msg: Message ->
            val G: AbstractGame? = msg.fetchGameInChannel()
            G is DiceGame && (G !is TurnBasedGame || !G.isCurrentTurn(msg.author))
        }
    ),

    MONOPOLY_GAME(commands = arrayOf(
        BotCommand(
            invocations = arrayOf("buy", "purchase"),
            descriptor = "Purchase the square that the player is currently standing on",
            execute = { msg: Message, _: Array<String> ->
                val G: MonopolyGame = msg.fetchGameInChannel() as MonopolyGame
            },
            isInvocable = { msg: Message ->
                (msg.fetchGameInChannel() as TurnBasedGame).isCurrentTurn(msg.author)
            }
        )),
        categoryDescriptor = "Commands for Monopoly game",
        isInvocableByMessage = { msg: Message ->
            msg.fetchGameInChannel() is MonopolyGame
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
