package utils

import bot.command.BotCommand
import bot.command.CommandCategory
import dev.kord.common.annotation.KordExperimental
import dev.kord.common.annotation.KordUnsafe
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.MessageChannelBehavior
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.behavior.reply
import dev.kord.core.entity.Message
import dev.kord.rest.builder.message.create.UserMessageCreateBuilder

suspend fun Message.respond(s: String): Unit {
    this.channel.createMessage(s)
}

suspend fun Message.respond(builder: UserMessageCreateBuilder.() -> Unit): Unit {
    this.channel.createMessage(builder)
}

suspend fun Message.reply(s: String): Unit {
    this.reply { this.content = s }
}

fun Message.canInvokeCommand(cat: CommandCategory): Boolean = cat.isInvocableByMessage(this)

fun Message.canInvokeCommand(cmd: BotCommand): Boolean =
    cmd.category.isInvocableByMessage(this) && cmd.isInvocable(this)

fun Message.canInvokeCommand(): Boolean = this.getCommand(true) != null

fun Message.getCommand(checkInvocable: Boolean = false): BotCommand? {
    val invocation: String = getArgs(this.content)[0].substring(1).lowercase()

    return CommandCategory.values()
        .filter { cat: CommandCategory -> !checkInvocable || cat.isInvocableByMessage(this) }
        .map { cat: CommandCategory -> cat.commands }
        .reduce { acc: Array<BotCommand>, c: Array<BotCommand> -> acc + c }
        .firstOrNull { cmd: BotCommand ->
            return@firstOrNull invocation in cmd.invocations && (!checkInvocable || cmd.isInvocable(this))
        }
}

@OptIn(KordUnsafe::class, KordExperimental::class)
fun Kord.getChannel(id: ULong): MessageChannelBehavior = unsafe.messageChannel(Snowflake(id))

fun getArgs(s: String): Array<String> = s
    .split(" (?=(?:[^\"]*\"[^\"]*\")*[^\"]*\$)".toRegex())
    .filter { x -> x.isNotBlank() }
    .toTypedArray()

fun formatDiceRoll(roll: ArrayList<Int>, showSum: Boolean = true): String =
    roll.joinToString(" ") { n: Int ->
        when (n) {
            1 -> BotConstants.DICE_1
            2 -> BotConstants.DICE_2
            3 -> BotConstants.DICE_3
            4 -> BotConstants.DICE_4
            5 -> BotConstants.DICE_5
            6 -> BotConstants.DICE_6
            else -> "[ $n ]"
        }
    } + (if (showSum) " = **${roll.sum()}**" else "")
